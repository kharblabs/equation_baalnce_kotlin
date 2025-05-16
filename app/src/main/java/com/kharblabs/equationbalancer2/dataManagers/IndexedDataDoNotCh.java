package com.kharblabs.equationbalancer2.dataManagers;


import androidx.annotation.Keep;

import java.io.Serializable;

@Keep
public class IndexedDataDoNotCh implements Serializable {
    public final String[] allCompounds;
    private static final long serialVersionUID = -8311006132297305254L;
    public final int[] allCompoundsCount;
    public final String[] productKeys;
    public final String[][] reactionsIndexedByProductKeys;
    public final String[][] reactionsIndexedByReagentKeys;
    public final String[] reagentKeys;
    // indexed data (reagentKeys,
    //               reaction indexed by reagent,
    //               product keys,
    //                reactionindexed by prod keys,
    //               all compound
    //                compund count)
    public IndexedDataDoNotCh(String[] strArr, String[][] strArr2, String[] strArr3, String[][] strArr4, String[] strArr5, int[] iArr) {
        this.reagentKeys = strArr;
        this.reactionsIndexedByReagentKeys = strArr2;
        this.productKeys = strArr3;
        this.reactionsIndexedByProductKeys = strArr4;
        this.allCompounds = strArr5;
        this.allCompoundsCount = iArr;
    }
}
