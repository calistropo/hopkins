package com.calistropo.util

object Idioms {

  implicit class AnyOps[T](val t: T) extends AnyVal {
    def |>[U](func: T => U): U = {
      func(t)
    }
  }

}
