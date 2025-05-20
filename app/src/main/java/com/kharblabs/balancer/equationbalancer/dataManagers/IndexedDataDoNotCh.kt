package com.kharblabs.balancer.equationbalancer.dataManagers

import androidx.annotation.Keep
import java.io.Serializable


@Keep

data class IndexedDataDoNotCh2(
    val reagentKeys: Array<String>,
    val reactionsIndexedByReagentKeys: Array<Array<String>>,
    val productKeys: Array<String>,
    val reactionsIndexedByProductKeys: Array<Array<String>>,
    val allCompounds: Array<String>,
    val allCompoundsCount: IntArray
) : Serializable
/*{
    companion object {
        // Update this to the serialVersionUID from the serialized version
        private const val serialVersionUID: Long = 5651863890238941535L
    }
}*/