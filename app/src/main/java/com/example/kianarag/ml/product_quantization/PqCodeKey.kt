package com.example.kianarag.ml.product_quantization

import java.util.function.IntFunction

class PqCodeKey(val backing: IntArray) {
    override fun equals(other: Any?): Boolean {
        return other is PqCodeKey && backing.contentEquals(other.backing)
    }

    override fun hashCode(): Int = backing.contentHashCode()


}

