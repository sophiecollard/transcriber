package com.github.sophiecollard.hangeul4s.model.hangeul

import cats.instances.vector._
import com.github.sophiecollard.hangeul4s.model.hangeul.HangeulJamo._
import com.github.sophiecollard.hangeul4s.model.hangeul.HangeulSyllabicBlock._
import com.github.sophiecollard.hangeul4s.model.hangeul.HangeulTextElement._
import com.github.sophiecollard.hangeul4s.parsing.{AccumulativeParser, Parser}
import com.github.sophiecollard.hangeul4s.parsing.instances._
import org.specs2.mutable.Specification

class HangeulTextElementSpec extends Specification {

  "HangeulTextElement#toString" should {

    "convert a Captured instance to a string" in {
      val element = HangeulTextElement.Captured.fromSyllabicBlocks(
        ThreeLetter(Initial.ㅇ, Medial.ㅏ, Final.ㄴ),
        ThreeLetter(Initial.ㄴ, Medial.ㅕ, Final.ㅇ),
        TwoLetter(Initial.ㅎ, Medial.ㅏ),
        TwoLetter(Initial.ㅅ, Medial.ㅔ),
        TwoLetter(Initial.ㅇ, Medial.ㅛ)
      )

      element.toString ==== "안녕하세요"
    }

    "convert a NotCaptured instance to a string" in {
      val element = HangeulTextElement.NotCaptured.unvalidatedFrom("!#&0123456789")

      element.toString ==== "!#&0123456789"
    }

  }

  "HangeulTextElement#failFastParser" should {

    "parse a single element made of characters from the Hangeul Syllables Unicode block" in {
      val input = " 안녕하세요"

      val expectedOutput = Vector[HangeulTextElement](
        HangeulTextElement.Captured.fromSyllabicBlocks(
          ThreeLetter(Initial.ㅇ, Medial.ㅏ, Final.ㄴ),
          ThreeLetter(Initial.ㄴ, Medial.ㅕ, Final.ㅇ),
          TwoLetter(Initial.ㅎ, Medial.ㅏ),
          TwoLetter(Initial.ㅅ, Medial.ㅔ),
          TwoLetter(Initial.ㅇ, Medial.ㅛ)
        )
      )

      Parser[Vector[HangeulTextElement]].parse(input) must beRight(expectedOutput)
    }

    "parse a single element made of other characters" in {
      val input = " !#&0123456789"

      val expectedOutput = Vector[HangeulTextElement](HangeulTextElement.NotCaptured.unvalidatedFrom(input.trim))

      Parser[Vector[HangeulTextElement]].parse(input) must beRight(expectedOutput)
    }

    "parse whitespace-separated Hangeul text elements" in {
      // first sentence of second paragraph of the Korean Wikipedia article on Seoul (retrieved 2019-09-22)
      // See https://ko.wikipedia.org/wiki/%EC%84%9C%EC%9A%B8%ED%8A%B9%EB%B3%84%EC%8B%9C
      val input = "시청 소재지는 중구이며, 25개의 자치구로 이루어져 있다."

      val expectedOutput = Vector[HangeulTextElement](
        HangeulTextElement.Captured.fromSyllabicBlocks(
          TwoLetter(Initial.ㅅ, Medial.ㅣ),
          ThreeLetter(Initial.ㅊ, Medial.ㅓ, Final.ㅇ)
        ),
        HangeulTextElement.Captured.fromSyllabicBlocks(
          TwoLetter(Initial.ㅅ, Medial.ㅗ),
          TwoLetter(Initial.ㅈ, Medial.ㅐ),
          TwoLetter(Initial.ㅈ, Medial.ㅣ),
          ThreeLetter(Initial.ㄴ, Medial.ㅡ, Final.ㄴ)
        ),
        HangeulTextElement.Captured.fromSyllabicBlocks(
          ThreeLetter(Initial.ㅈ, Medial.ㅜ, Final.ㅇ),
          TwoLetter(Initial.ㄱ, Medial.ㅜ),
          TwoLetter(Initial.ㅇ, Medial.ㅣ),
          TwoLetter(Initial.ㅁ, Medial.ㅕ)
        ),
        HangeulTextElement.NotCaptured.unvalidatedFrom(","),
        HangeulTextElement.NotCaptured.unvalidatedFrom("25"),
        HangeulTextElement.Captured.fromSyllabicBlocks(
          TwoLetter(Initial.ㄱ, Medial.ㅐ),
          TwoLetter(Initial.ㅇ, Medial.ㅢ)
        ),
        HangeulTextElement.Captured.fromSyllabicBlocks(
          TwoLetter(Initial.ㅈ, Medial.ㅏ),
          TwoLetter(Initial.ㅊ, Medial.ㅣ),
          TwoLetter(Initial.ㄱ, Medial.ㅜ),
          TwoLetter(Initial.ㄹ, Medial.ㅗ)
        ),
        HangeulTextElement.Captured.fromSyllabicBlocks(
          TwoLetter(Initial.ㅇ, Medial.ㅣ),
          TwoLetter(Initial.ㄹ, Medial.ㅜ),
          TwoLetter(Initial.ㅇ, Medial.ㅓ),
          TwoLetter(Initial.ㅈ, Medial.ㅕ)
        ),
        HangeulTextElement.Captured.fromSyllabicBlocks(
          ThreeLetter(Initial.ㅇ, Medial.ㅣ, Final.ㅆ),
          TwoLetter(Initial.ㄷ, Medial.ㅏ)
        ),
        HangeulTextElement.NotCaptured.unvalidatedFrom(".")
      )

      Parser[Vector[HangeulTextElement]].parse(input) must beRight(expectedOutput)
    }

  }

  "HangeulTextElement#accumulativeParser" should {

    "parse a single element made of characters from the Hangeul Syllables Unicode block" in {
      val input = " 안녕하세요"

      val expectedOutput = Vector[HangeulTextElement](
        HangeulTextElement.Captured.fromSyllabicBlocks(
          ThreeLetter(Initial.ㅇ, Medial.ㅏ, Final.ㄴ),
          ThreeLetter(Initial.ㄴ, Medial.ㅕ, Final.ㅇ),
          TwoLetter(Initial.ㅎ, Medial.ㅏ),
          TwoLetter(Initial.ㅅ, Medial.ㅔ),
          TwoLetter(Initial.ㅇ, Medial.ㅛ)
        )
      )

      AccumulativeParser[Vector[HangeulTextElement]].parse(input).toEither must beRight(expectedOutput)
    }

    "parse a single element made of other characters" in {
      val input = " !#&0123456789"

      val expectedOutput = Vector[HangeulTextElement](HangeulTextElement.NotCaptured.unvalidatedFrom(input.trim))

      AccumulativeParser[Vector[HangeulTextElement]].parse(input).toEither must beRight(expectedOutput)
    }

    "parse whitespace-separated Hangeul text elements" in {
      // first sentence of second paragraph of the Korean Wikipedia article on Seoul (retrieved 2019-09-22)
      // See https://ko.wikipedia.org/wiki/%EC%84%9C%EC%9A%B8%ED%8A%B9%EB%B3%84%EC%8B%9C
      val input = "시청 소재지는 중구이며, 25개의 자치구로 이루어져 있다."

      val expectedOutput = Vector[HangeulTextElement](
        HangeulTextElement.Captured.fromSyllabicBlocks(
          TwoLetter(Initial.ㅅ, Medial.ㅣ),
          ThreeLetter(Initial.ㅊ, Medial.ㅓ, Final.ㅇ)
        ),
        HangeulTextElement.Captured.fromSyllabicBlocks(
          TwoLetter(Initial.ㅅ, Medial.ㅗ),
          TwoLetter(Initial.ㅈ, Medial.ㅐ),
          TwoLetter(Initial.ㅈ, Medial.ㅣ),
          ThreeLetter(Initial.ㄴ, Medial.ㅡ, Final.ㄴ)
        ),
        HangeulTextElement.Captured.fromSyllabicBlocks(
          ThreeLetter(Initial.ㅈ, Medial.ㅜ, Final.ㅇ),
          TwoLetter(Initial.ㄱ, Medial.ㅜ),
          TwoLetter(Initial.ㅇ, Medial.ㅣ),
          TwoLetter(Initial.ㅁ, Medial.ㅕ)
        ),
        HangeulTextElement.NotCaptured.unvalidatedFrom(","),
        HangeulTextElement.NotCaptured.unvalidatedFrom("25"),
        HangeulTextElement.Captured.fromSyllabicBlocks(
          TwoLetter(Initial.ㄱ, Medial.ㅐ),
          TwoLetter(Initial.ㅇ, Medial.ㅢ)
        ),
        HangeulTextElement.Captured.fromSyllabicBlocks(
          TwoLetter(Initial.ㅈ, Medial.ㅏ),
          TwoLetter(Initial.ㅊ, Medial.ㅣ),
          TwoLetter(Initial.ㄱ, Medial.ㅜ),
          TwoLetter(Initial.ㄹ, Medial.ㅗ)
        ),
        HangeulTextElement.Captured.fromSyllabicBlocks(
          TwoLetter(Initial.ㅇ, Medial.ㅣ),
          TwoLetter(Initial.ㄹ, Medial.ㅜ),
          TwoLetter(Initial.ㅇ, Medial.ㅓ),
          TwoLetter(Initial.ㅈ, Medial.ㅕ)
        ),
        HangeulTextElement.Captured.fromSyllabicBlocks(
          ThreeLetter(Initial.ㅇ, Medial.ㅣ, Final.ㅆ),
          TwoLetter(Initial.ㄷ, Medial.ㅏ)
        ),
        HangeulTextElement.NotCaptured.unvalidatedFrom(".")
      )

      AccumulativeParser[Vector[HangeulTextElement]].parse(input).toEither must beRight(expectedOutput)
    }

  }

}
