package com.github.boybeak.fltwgt.magnetic

sealed class Side {
    abstract val direction: Int

    object Start : Side() { override val direction = 0b0001 }
    object End : Side() { override val direction = 0b0010 }
    object Top : Side() { override val direction = 0b0100 }
    object Bottom : Side() { override val direction = 0b1000 }

    data class Combined(val sides: Set<Side>) : Side() {
        override val direction: Int = sides.fold(0) { acc, side -> acc or side.direction }

        constructor(vararg sides: Side) : this(sides.toSet())

        operator fun plus(other: Side): Combined = Combined(sides + other)
        operator fun minus(other: Side): Combined = Combined(sides - other)
        operator fun contains(other: Side): Boolean = other in sides
    }

    companion object {
        val Horizontal = Combined(Start, End)
        val Vertical = Combined(Top, Bottom)
        val All = Combined(Start, End, Top, Bottom)
    }
}