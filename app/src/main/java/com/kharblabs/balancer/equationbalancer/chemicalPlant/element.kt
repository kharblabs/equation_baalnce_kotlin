package com.kharblabs.balancer.equationbalancer.chemicalPlant

data class element(var name: String, var count: Float) {
    fun setCount(count: Int) {
        this.count = count.toFloat()
    }

}

