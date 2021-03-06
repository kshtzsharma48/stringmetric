package com.rockymadden.stringmetric.similarity

import com.rockymadden.stringmetric.{ CompareTuple, StringMetric }

/** An implementation of the Levenshtein metric. */
class LevenshteinMetric extends StringMetric[Int] {
	final override def compare(charArray1: Array[Char], charArray2: Array[Char]): Option[Int] = {
		val fca1 = filter(charArray1)
		lazy val fca2 = filter(charArray2)

		if (fca1.length == 0 || fca2.length == 0) None
		else if (fca1.sameElements(fca2)) Some(0)
		else Some(levenshtein(fca1, fca2))
	}

	final override def compare(string1: String, string2: String): Option[Int] =
		compare(filter(string1.toCharArray), filter(string2.toCharArray))

	private[this] def levenshtein(ct: CompareTuple[Char]) = {
		val m = Array.fill[Int](ct._1.length + 1, ct._2.length + 1)(-1)

		def distance(t: (Int, Int)): Int = {
			t match {
				case (r, 0) => r
				case (0, c) => c
				case (r, c) if m(r)(c) != -1 => m(r)(c)
				case (r, c) => {
					val min = {
						if (ct._1(r - 1) == ct._2(c - 1)) distance(r - 1, c - 1)
						else math.min(
							math.min(
								distance(r - 1, c) + 1, // Delete (left).
								distance(r, c - 1) + 1 // Insert (up).
							),
							distance(r - 1, c - 1) + 1 // Substitute (left-up).
						)
					}

					m(r)(c) = min
					min
				}
			}
		}

		distance(ct._1.length, ct._2.length)
	}
}

object LevenshteinMetric {
	def apply(): LevenshteinMetric = new LevenshteinMetric
}
