package org.polyfrost.polynametag.util

import gg.essential.handlers.OnlineIndicator
import org.polyfrost.polynametag.PolyNametag

fun currentlyDrawingEntityName() = PolyNametag.isEssential && OnlineIndicator.currentlyDrawingEntityName()