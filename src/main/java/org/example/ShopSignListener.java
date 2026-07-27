package org.example;

import com.dre.brewery.Brew;
import com.dre.brewery.api.BreweryApi;
import com.earth2me.essentials.api.Economy;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class ShopSignListener implements Listener {

    private record ValidationResult(boolean valid, List<String> errors, String drinkName, String quality, double price) {
        static ValidationResult invalid(List<String> errors) {
            return new ValidationResult(false, errors, null, null, 0);
        }
    }

    @EventHandler
    public void onSignClick(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        Block block = event.getClickedBlock();
        if (block == null || !(block.getState() instanceof Sign sign)) return;

        String[] lines = sign.getLines();
        String header = lines[0].replaceAll("§[0-9a-fk-or]", "").trim();
        if (!header.equalsIgnoreCase("[BuyBrew]") && !header.equalsIgnoreCase("[SellBrew]")) return;

        event.setCancelled(true);
        Player player = event.getPlayer();

        ValidationResult result = validateSign(lines);
        if (!result.valid()) {
            player.openSign(sign);
            return;
        }

        if (lines[0].startsWith("§4")) {
            sign.setLine(0, "§1" + header);
            sign.update();
        }

        if (header.equalsIgnoreCase("[BuyBrew]")) {
            handleBuy(player, result.drinkName(), result.quality(), result.price());
        } else {
            handleSell(player, result.drinkName(), result.quality(), result.price());
        }
    }

    @EventHandler
    public void onSignCreate(SignChangeEvent event) {
        String raw = event.getLine(0);
        if (raw == null) return;
        String header = raw.replaceAll("§[0-9a-fk-or]", "").trim();

        if (!header.equalsIgnoreCase("[BuyBrew]") && !header.equalsIgnoreCase("[SellBrew]")) return;

        if (!event.getPlayer().hasPermission("brewtrade.sign.create")) {
            event.getPlayer().sendMessage("§cНет прав на создание этой таблички!");
            event.setCancelled(true);
            return;
        }

        String[] lines = new String[]{
                event.getLine(0) == null ? "" : event.getLine(0),
                event.getLine(1) == null ? "" : event.getLine(1),
                event.getLine(2) == null ? "" : event.getLine(2),
                event.getLine(3) == null ? "" : event.getLine(3)
        };

        ValidationResult result = validateSign(lines);

        String color = "§";
        if (result.valid()) {
            color += "1";

        } else {
            color += "4";
            for (String err : result.errors()) {
                event.getPlayer().sendMessage("§c- " + err);
            }
        }
        if (header.equalsIgnoreCase("[BuyBrew]")) {
            event.setLine(0, color + "[BuyBrew]");
        } else if (header.equalsIgnoreCase("[SellBrew]")) {
            event.setLine(0, color + "[SellBrew]");
        }
        if (lines[3].charAt(0) != '$') {
            event.setLine(3, "$" + lines[3]);
        }
    }

    private ValidationResult validateSign(String[] lines) {
        List<String> errors = new ArrayList<>();

        String drinkName = lines.length > 1 ? lines[1].trim() : "";
        String qualityRaw = lines.length > 2 ? lines[2].trim() : "";
        String quality = qualityRaw.toLowerCase();
        String priceRaw = lines.length > 3 ? lines[3].trim() : "";

        Brew drink = BreweryApi.createBrew(drinkName, qualityToInt(quality));
        if (drinkName.isEmpty()) {
            errors.add("Название напитка не указано.");
        } else if (drink == null) {
            errors.add("Название напитка не соответствует конфигурации.");
        }

        if (!quality.equals("poor") && !quality.equals("normal") && !quality.equals("good")) {
            errors.add("Качество должно быть 'poor', 'normal' или 'good'.");
        }

        double price = -1;
        if (priceRaw.isEmpty()) {
            errors.add("Цена не указана.");
        } else {
            try {

                price = Double.parseDouble(priceRaw.replace("$", "").trim());
                if (price <= 0) {
                    errors.add("Цена должна быть больше нуля.");
                }
            } catch (NumberFormatException e) {
                errors.add(String.valueOf(e));
            }
        }

        if (!errors.isEmpty()) {
            return ValidationResult.invalid(errors);
        }

        return new ValidationResult(true, errors, drinkName, quality, price);
    }

    private int qualityToInt(String quality) {
        return switch (quality.toLowerCase()) {
            case "poor" -> 1;
            case "normal" -> 5;
            case "good" -> 10;
            default -> 5;
        };
    }

    private void handleBuy(Player player, String drinkName, String quality, double price) {
        try {
            BigDecimal balance = Economy.getMoneyExact(player.getUniqueId());
            if (balance.compareTo(BigDecimal.valueOf(price)) < 0) {
                player.sendMessage("§cНедостаточно средств!");
                return;
            }

            Brew drink = BreweryApi.createBrew(drinkName, qualityToInt(quality));
            if (drink == null) {
                player.sendMessage("§cНапиток не найден: " + drinkName);
                return;
            }

            Economy.subtract(player.getUniqueId(), BigDecimal.valueOf(price));
            player.getInventory().addItem(drink.createItem());

            player.sendMessage("§6$" + price + " §ahas been taken from your account.");

        } catch (Exception e) {
            player.sendMessage("§cОшибка при покупке: " + e.getMessage());
        }
    }

    private void handleSell(Player player, String drinkName, String quality, double price) {
        try {
            ItemStack[] contents = player.getInventory().getContents();
            int found = 0;

            for (ItemStack item : contents) {
                if (item == null || !BreweryApi.isBrew(item)) continue;
                Brew brew = BreweryApi.getBrew(item);
                if (brew == null || brew.getCurrentRecipe() == null) continue;
                if (matchesName(brew, drinkName, quality)) {
                    found += item.getAmount();
                }
            }

            if (found < 1) {
                player.sendMessage("§cНедостаточно напитков в инвентаре!");
                return;
            }

            int toRemove = 1;
            for (ItemStack item : contents) {
                if (toRemove <= 0) break;
                if (item == null || !BreweryApi.isBrew(item)) continue;
                Brew brew = BreweryApi.getBrew(item);
                if (brew == null || brew.getCurrentRecipe() == null) continue;
                if (matchesName(brew, drinkName, quality)) {
                    if (item.getAmount() <= toRemove) {
                        toRemove -= item.getAmount();
                        item.setAmount(0);
                    } else {
                        item.setAmount(item.getAmount() - toRemove);
                        toRemove = 0;
                    }
                }
            }

            Economy.add(player.getUniqueId(), BigDecimal.valueOf(price));
            player.sendMessage("§6$" + price + " §ahas been added to your account.");

        } catch (Exception e) {
            player.sendMessage("§cОшибка при продаже: " + e.getMessage());
        }
    }

    private boolean matchesName(Brew brew, String drinkName, String quality) {
        String[] names = brew.getCurrentRecipe().getName();
        boolean recipeMatches = false;
        for (String n : names) {
            if (n != null && n.replaceAll("§[0-9a-fk-or]", "").trim()
                    .replaceAll("\\s+", "")
                    .equalsIgnoreCase(drinkName.replaceAll("\\s+", ""))) {
                recipeMatches = true;
                break;
            }
        }
        if (!recipeMatches) return false;

        int q = brew.getQuality();
        return switch (quality.toLowerCase()) {
            case "poor" -> q >= 1 && q <= 3;
            case "normal" -> q >= 4 && q <= 7;
            case "good" -> q >= 8 && q <= 10;
            default -> false;
        };
    }
}