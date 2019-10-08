package com.github.sophiecollard.hangeul4s.encoding

import com.github.sophiecollard.hangeul4s.error.DecodingError

trait Codec[A, B] extends Decoder[A, B] with Encoder[B, A]

object Codec {

  def apply[A, B](implicit ev: Codec[A, B]): Codec[A, B] =
    ev

  def apply[A, B](implicit ev1: Decoder[A, B], ev2: Encoder[B, A]): Codec[A, B] =
    Codec.instance(ev1.decode, ev2.encode)

  def instance[A, B](f: A => Either[DecodingError, B], g: B => A): Codec[A, B] =
    new Codec[A, B] {
      override def decode(encoded: A): Either[DecodingError, B] =
        f(encoded)

      override def encode(decoded: B): A =
        g(decoded)
    }

}
