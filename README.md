# PolyNametag

[![Modrinth downloads](https://img.shields.io/modrinth/dt/HK3p7LMj?style=flat&logo=modrinth&label=Modrinth&color=1bd96a)](https://modrinth.com/mod/polynametag)
[![CurseForge downloads](https://img.shields.io/curseforge/dt/1147717?style=flat&logo=curseforge&label=CurseForge&color=f16436)](https://www.curseforge.com/minecraft/mc-mods/polynametag)

PolyNametag changes how the nametags above players and other entities look. It also shows your own nametag in third person.

## Features

- Raise or lower nametags with a height offset slider from -10 to 10, in steps of 0.25.
- Shrink nametags with a scale slider from 0 to 1, in steps of 0.05.
- Turn on a text shadow. It is off by default.
- Set the text color. The default is white.
- Change the background color, or turn the background off.
- Round the corners of the background with a radius from 0 to 10. This is off by default.
- Add horizontal and vertical padding between the text and the edge of the background, from 0 to 10 each.
- Remove every nametag with one switch. It is off by default.

![A player nametag with a blue background](screenshots/nametag-showcase.png)

### Your own nametag

PolyNametag shows your own nametag in third person. This is on by default. It leaves your nametag out when the server already displays one with your name above your head, so you do not see two.

"Show in inventory" also draws the nametag on the player model in your inventory screen. It is off by default.

### Nametags while the HUD is hidden

Three switches decide which nametags disappear while you hide the HUD with F1. One covers players, one covers armor stands and one covers every other entity. All three are on by default, which matches vanilla. Turn one off to keep those nametags on screen.

## Supported versions

- Minecraft 1.8.9 on Ornithe.
- Minecraft 1.21.1, 1.21.4, 1.21.5, 1.21.8, 1.21.10, 1.21.11, 26.1, 26.2 and 26.3 on Fabric.

PolyNametag needs [OneConfig](https://modrinth.com/mod/oneconfig) and Fabric Language Kotlin.

## Support

- Report bugs and ask questions on the [Polyfrost Discord](https://polyfrost.org/discord).
- Check [Polyfrost Status](https://status.polyfrost.org/) if a Polyfrost service is down.

## Building

```sh
./gradlew ":26.3:build"
```

Replace `26.3` with any supported Minecraft version. The build needs JDK 21 and JDK 25 installed.
