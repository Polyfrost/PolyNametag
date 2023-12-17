<img align="right" src="src/main/resources/polynametag.svg"/>

# PolyNametag

![Compact Powered by OneConfig](https://polyfrost.org/img/compact_vector.svg)


A nametag modification mod

## Features

- Height offset
- Text shadow
- Show own nametag
- Background
- Background Color

![nametag-showcase.png](screenshots/nametag-showcase.png)

## Settings Page 

![settings-page.png](screenshots/settings-page.png)

## Note to contributors: Gradle structure

You may notice the Gradle structure for this project is a bit different than usual. Essentially (pun intended), while trying to add support for Essential's nametag feature, I found that my usual approach with dummy classes wasn't working; this is because the method I was trying to access uses Minecraft classes, which I couldn't get working in the dummy source set. I really, really didn't want to use reflection, especially in this context where performance is extremely important. Thus, I had to separate the "dummy" classes I made with the actual mod using subprojects.

The `mod` subproject is the actual mod, and the `mod-compat` subproject is the dummy classes. The root project and the `mod-compat` subproject's builds should NOT be used for anything other than compiling the main `mod` subproject.

If anyone has a better solution to this, please let me know.