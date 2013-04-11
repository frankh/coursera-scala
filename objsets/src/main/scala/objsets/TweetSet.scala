package objsets

import common._
import TweetReader._

/**
 * A class to represent tweets.
 */
class Tweet(val user: String, val text: String, val retweets: Int) {
	override def toString: String =
	"User: " + user + "\n" +
	"Text: " + text + " [" + retweets + "]"
}


abstract class TweetSet {


	def filter(p: Tweet => Boolean): TweetSet = filterAcc(p,new Empty)

 
	def filterAcc(p: Tweet => Boolean, acc: TweetSet): TweetSet


	 def union(that: TweetSet): TweetSet 
	 
	 def empty:Boolean

	/**
	 * Returns the tweet from this set which has the smallest retweet count.
	 *
	 * Calling `mostRetweeted` on an empty set should throw an exception of
	 * type `java.util.NoSuchElementException`.
	 *
	 * Question: Should we implment this method here, or should it remain abstract
	 * and be implemented in the subclasses?
	 */
	def mostRetweeted: Tweet 

	/**
	 * Returns a list containing all tweets of this set, sorted by retweet count
	 * in descending order. In other words, the head of the resulting list should
	 * have the highest retweet count.
	 *
	 * Hint: the method `remove` on TweetSet will be very useful.
	 * Question: Should we implment this method here, or should it remain abstract
	 * and be implemented in the subclasses?
	 */
	def descendingByRetweet: TweetList = {
			def order(set: TweetSet,list:TweetList):TweetList=
			{
				list foreach println
					if(set.empty) list
					
					else 
					{
						def rem:Tweet = set.mostRetweeted
						println(rem.retweets)
						order(set.remove(rem), new Cons(rem,list))
					}
			}
			order(this, Nil)
	}


	/**
	 * The following methods are already implemented
	 */

	/**
	 * Returns a new `TweetSet` which contains all elements of this set, and the
	 * the new element `tweet` in case it does not already exist in this set.
	 *
	 * If `this.contains(tweet)`, the current set is returned.
	 */
	def incl(tweet: Tweet): TweetSet

	/**
	 * Returns a new `TweetSet` which excludes `tweet`.
	 */
	def remove(tweet: Tweet): TweetSet

	/**
	 * Tests if `tweet` exists in this `TweetSet`.
	 */
	def contains(tweet: Tweet): Boolean

	/**
	 * This method takes a function and applies it to every element in the set.
	 */
	def foreach(f: Tweet => Unit): Unit
}

class Empty extends TweetSet {
	
	def empty = true

	def filterAcc(p: Tweet => Boolean, acc: TweetSet): TweetSet = acc
	
	def union(that: TweetSet): TweetSet =that

	def mostRetweeted: Tweet = throw new NoSuchElementException()
	/**
	 * The following methods are already implemented
	 */

	def contains(tweet: Tweet): Boolean = false

	def incl(tweet: Tweet): TweetSet = new NonEmpty(tweet, new Empty, new Empty)

	def remove(tweet: Tweet): TweetSet = this

	def foreach(f: Tweet => Unit): Unit = ()
	
	
}

class NonEmpty(elem: Tweet, left: TweetSet, right: TweetSet) extends TweetSet {

	def empty = false
	
	def filterAcc(p: Tweet => Boolean, acc: TweetSet): TweetSet = {
		if(p(elem)) left.filterAcc(p,right.filterAcc(p,acc.incl(elem)))
		else left.filterAcc(p,right.filterAcc(p,acc))
	}

	def union(that: TweetSet): TweetSet = {
		if (that.empty) this
		else right union ((that.incl(elem)) union left) 
	 
	}

	
	 def mostRetweeted: Tweet = {
	
		
		def biggest:Tweet = {
			lazy val rb =right.mostRetweeted

			lazy val lb =left.mostRetweeted

			if(right.empty && left.empty) new Tweet("empty","empty",100000)
			else if (right.empty)lb
			else if(left.empty) rb
			else if(lb.retweets < rb.retweets) lb
			else rb
		}
		 if (elem.retweets < biggest.retweets) elem
		 else biggest


		 /*
			if((TS.right).empty &&(TS.left).empty &&(TS.elem).retweets>X.retweets) check(TS.left,check(TS.right, TS.elem))
			 else if((TS.left).empty &&(TS.elem).retweets>X.retweets) check(TS.right,TS.elem)
			 else if((TS.right).empty &&(TS.elem).retweets>X.retweets) check(TS.left,TS.elem)
			 else if((TS.right).empty &&(TS.left).empty) X
			 else if((TS.left).empty) check(TS.right,X)
			 else if((TS.right).empty) check(TS.left,X)
			 else check(TS.left,check(TS.right, X)) */
		
	 }
	/**
	 * The following methods are already implemented
	 */

	def contains(x: Tweet): Boolean =
	if (x.text < elem.text) left.contains(x)
	else if (elem.text < x.text) right.contains(x)
	else true

	def incl(x: Tweet): TweetSet = {
	if (x.text < elem.text) new NonEmpty(elem, left.incl(x), right)
	else if (elem.text < x.text) new NonEmpty(elem, left, right.incl(x))
	else this
	}

	def remove(tw: Tweet): TweetSet =
	if (tw.text < elem.text) new NonEmpty(elem, left.remove(tw), right)
	else if (elem.text < tw.text) new NonEmpty(elem, left, right.remove(tw))
	else left.union(right)

	def foreach(f: Tweet => Unit): Unit = {
	f(elem)
	left.foreach(f)
	right.foreach(f)
	}
}

trait TweetList {
	def head: Tweet
	def tail: TweetList
	def isEmpty: Boolean
	def foreach(f: Tweet => Unit): Unit =
	if (!isEmpty) {
		f(head)
		tail.foreach(f)
	}
}

object Nil extends TweetList {
	def head = throw new java.util.NoSuchElementException("head of EmptyList")
	def tail = throw new java.util.NoSuchElementException("tail of EmptyList")
	def isEmpty = true
}

class Cons(val head: Tweet, val tail: TweetList) extends TweetList {
	def isEmpty = false
}


object GoogleVsApple {
	val google = List("android", "Android", "galaxy", "Galaxy", "nexus", "Nexus")
	val apple = List("ios", "iOS", "iphone", "iPhone", "ipad", "iPad")

def goo(t:Tweet):Boolean= google.exists(tex=>(t.text).contains(tex))

def app(t:Tweet):Boolean= apple.exists(tex=>(t.text).contains(tex))


	lazy val googleTweets: TweetSet = (TweetReader.allTweets).filter(goo)
	lazy val appleTweets: TweetSet = (TweetReader.allTweets).filter(app)
	/**
	 * A list of all tweets mentioning a keyword from either apple or google,
	 * sorted by the number of retweets.
	 */
	lazy val trending: TweetList = (googleTweets union appleTweets).descendingByRetweet
}

object Main extends App {
	// Print the trending tweets
	GoogleVsApple.trending foreach println
}