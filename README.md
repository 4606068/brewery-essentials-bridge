# BreweryEssentialsBridge

[🇬🇧 English](README.md) | [🇷🇺 Русский](README.ru.md)

A Paper plugin that bridges [BreweryX](https://github.com/BreweryTeam/BreweryX) and [EssentialsX](https://github.com/EssentialsX/Essentials), adding `[BuyBrew]` / `[SellBrew]` shop signs so players can buy and sell Brewery drinks using Essentials' economy.



## Features

- `[BuyBrew]` and `[SellBrew]` shop signs

- Buying/selling Brewery drinks with quality taken into account

- Money withdrawal/deposit via Essentials Economy (Vault-based)

- Sign creation permission (`brewtrade.sign.create`)

- Automatic sign validation (invalid signs are highlighted and reopened for editing)



## Dependencies

- [Paper](https://papermc.io/) 1.21.x

- [BreweryX](https://github.com/BreweryTeam/BreweryX) 3.7.0+

- [EssentialsX](https://essentialsx.net/) 2.21.0+



## Tech stack

- Java 21

- Gradle (Kotlin DSL)



## Building

```bash

./gradlew build

```

The compiled `.jar` will be in `build/libs/`.



## Installation

1\. Build the plugin or download a `.jar` from [Releases](../../releases)

2\. Make sure \*\*BreweryX\*\* and \*\*EssentialsX\*\* are already installed on your server

3\. Drop the `.jar` into your server's `plugins/` folder

4\. Restart the server



## Usage

Create a sign with the following lines:

```

[BuyBrew] / [SellBrew]

<drink name>

<quality>

<price>

```

The plugin validates the sign automatically and reopens it for editing if the data is invalid.



## License

MIT

