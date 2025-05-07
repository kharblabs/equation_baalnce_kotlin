package com.kharblabs.equationbalancer2.dataManagers

import androidx.annotation.Keep
import java.io.Serializable


@Keep
class IndexedDataDoNotCh // indexed data (reagentKeys,
//               reaction indexed by reagent,
//               product keys,
//                reactionindexed by prod keys,
//               all compound
//                compund count)
    (
    val reagentKeys: Array<String>,
    val reactionsIndexedByReagentKeys: Array<Array<String>>,
    val productKeys: Array<String>,
    val reactionsIndexedByProductKeys: Array<Array<String>>,
    val allCompounds: Array<String>,
    val allCompoundsCount: IntArray
) :
    Serializable
