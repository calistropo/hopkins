package com.barracuda.util

object Idioms {

  implicit class AnyOps[T](val t: T) extends AnyVal {
    def |>[U](func: T => U): U = {
      func(t)
    }
  }

}
