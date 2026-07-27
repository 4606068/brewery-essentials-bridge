# BreweryEssentialsBridge

[🇬🇧 English](README.md) | [🇷🇺 Русский](README.ru.md)

Аддон для Paper-сервера, связывающий [BreweryX](https://github.com/BreweryTeam/BreweryX) и [EssentialsX](https://github.com/EssentialsX/Essentials) — добавляет торговые знаки `[BuyBrew]` / `[SellBrew]` для покупки и продажи напитков Brewery через экономику Essentials.

## Возможности
- Знаки-магазины `[BuyBrew]` и `[SellBrew]`
- Покупка/продажа напитков Brewery с учётом качества
- Списание/начисление денег через Essentials Economy (на базе Vault)
- Право на создание знаков (`brewtrade.sign.create`)
- Автоматическая валидация знака (некорректный знак подсвечивается и открывается для редактирования)

## Зависимости
- [Paper](https://papermc.io/) 1.21.x
- [BreweryX](https://github.com/BreweryTeam/BreweryX) 3.7.0+
- [EssentialsX](https://essentialsx.net/) 2.21.0+

## Стек
- Java 21
- Gradle (Kotlin DSL)

## Сборка
```bash
./gradlew build
```
Готовый `.jar` появится в `build/libs/`.

## Установка
1. Соберите плагин или скачайте `.jar` из [Releases](../../releases)
2. Убедитесь, что на сервере уже установлены **BreweryX** и **EssentialsX**
3. Положите `.jar` в папку `plugins/` вашего сервера
4. Перезапустите сервер

## Использование
Создайте знак со следующими строками:
```
[BuyBrew] / [SellBrew]
<название напитка>
<качество>
<цена>
```
Плагин автоматически проверит знак и откроет его для редактирования, если данные введены неверно.

## Лицензия
MIT
