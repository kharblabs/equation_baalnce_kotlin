package com.kharblabs.equationbalancer.chemicalPlant

import org.junit.Assert.*

import org.junit.Test

 class ChemUtilsTest {

@Test
 fun bracketBreaker() {}

  @Test
  fun getMoleculeMass() {
  }

  @Test
  fun elementParser() {
   var s="Na2CO3(GaC2OOH)2"
   var chemUtils=ChemUtils()
   var t=chemUtils.elementParser(s)
   print(t)
   var elems=ArrayList<element>()
   elems.add(element("Na",2.0f))
   elems.add(element("C",5.0f))
   elems.add(element("O",7.0f))
   elems.add(element("Ga",2.0f))
   elems.add(element("H",2.0f))

   assertEquals(elems,t)

  }

  @Test
  fun groupAtomiser() {
  }

  @Test
  fun findOpenParen() {
  }

  @Test
  fun numparse() {
  }

  @Test
  fun atomiseandFind() {
   //arrange
   var chemUtils=ChemUtils()
  assertEquals(chemUtils.AtomiseandFind("Na2CO3(GaC2OOH)2","Ga"),2.0f)

  }
 }