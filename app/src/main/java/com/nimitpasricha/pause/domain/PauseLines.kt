package com.nimitpasricha.pause.domain

import kotlin.random.Random

/**
 * A line shown on the pause screen.
 *
 * The app casts itself as the user's scrappy ally, running interference against
 * an antagonist that is never the user — the feed, the algorithm, the infinite
 * scroll, the slot machine, the attention economy.
 */
sealed interface PauseLine {

    /** A single line narrating the little act of sabotage the app is performing. */
    data class Interference(val text: String) : PauseLine

    /**
     * A setup line that uses the wait as comic timing, then flips to a punchline
     * partway through. The timer drives the reveal: [setup] holds, then swaps to
     * [payoff] a couple of seconds before zero.
     */
    data class TwoBeat(val setup: String, val payoff: String) : PauseLine
}

/**
 * The pool of pause-screen copy, organized by escalation tier (1st open of the
 * day / 2nd / 3rd-and-beyond). Tone escalates with visit count — exasperation
 * aimed squarely at the situation, never the user, who by the high tiers gets
 * an affectionate nod. Every line stays tied to the feed/scroll/algorithm
 * theme; no generic filler.
 *
 * [pick] returns a random line for the visit, avoiding an immediate repeat.
 */
object PauseLines {

    fun pick(visitCount: Int, avoid: PauseLine? = null, random: Random = Random.Default): PauseLine {
        val pool = poolFor(visitCount)
        if (pool.size <= 1) return pool.first()
        while (true) {
            val candidate = pool[random.nextInt(pool.size)]
            if (candidate != avoid) return candidate
        }
    }

    private fun poolFor(visitCount: Int): List<PauseLine> = when {
        visitCount <= 1 -> tier1
        visitCount == 2 -> tier2
        else -> tier3
    }

    // --- Tier 1: first open of the day. Light, breezy. -----------------------

    private val tier1: List<PauseLine> = buildList {
        add(PauseLine.Interference("Wrestling the algorithm for your attention back…"))
        add(PauseLine.Interference("Putting the infinite scroll on hold…"))
        add(PauseLine.Interference("Confiscating the slot machine…"))
        add(PauseLine.Interference("Intercepting the dopamine delivery…"))
        add(PauseLine.Interference("Quietly unplugging the feed for a sec…"))
        add(PauseLine.Interference("Slipping a bookmark into the endless scroll…"))
        add(PauseLine.Interference("Asking the algorithm to take five…"))
        add(PauseLine.Interference("Catching the notifications mid-air…"))
        add(PauseLine.Interference("Putting the for-you page on pause…"))
        add(PauseLine.Interference("Holding the door shut on the timeline…"))
        add(PauseLine.Interference("Loosening the algorithm's grip, finger by finger…"))
        add(PauseLine.Interference("Stepping between you and the refresh button…"))
        add(PauseLine.Interference("Tucking the feed in for a quick nap…"))
        add(PauseLine.Interference("Pressing pause on the content firehose…"))
        add(PauseLine.Interference("Talking the autoplay out of autoplaying…"))
        add(PauseLine.Interference("Giving the scroll wheel a gentle hand-brake…"))
        add(PauseLine.Interference("Distracting the algorithm with a shiny object…"))
        add(PauseLine.Interference("Snipping the doomscroll thread…"))
        add(PauseLine.Interference("Standing the feed down for a moment…"))
        add(PauseLine.Interference("Letting the dopamine drip cool off…"))

        add(PauseLine.TwoBeat("Checking the feed for anything important…", "…nope, still nothing."))
        add(PauseLine.TwoBeat("Scanning for genuinely new posts…", "Same four you already saw."))
        add(PauseLine.TwoBeat("Looking for the thing you came here for…", "It was never in there."))
        add(PauseLine.TwoBeat("Asking the algorithm what's so urgent…", "It went quiet."))
        add(PauseLine.TwoBeat("Searching the timeline for breaking news…", "Just ads and a guy's lunch."))
        add(PauseLine.TwoBeat("Counting the must-see updates since you left…", "Zero. A clean zero."))
        add(PauseLine.TwoBeat("Refreshing to see what you missed…", "You missed nothing."))
        add(PauseLine.TwoBeat("Loading today's essential content…", "There isn't any."))
        add(PauseLine.TwoBeat("Polling the feed for genuine surprises…", "It shrugged."))
        add(PauseLine.TwoBeat("Double-checking for a reason to be here…", "Couldn't find one."))
        add(PauseLine.TwoBeat("Hunting for the post that changes everything…", "Still buffering. Forever."))
        add(PauseLine.TwoBeat("Seeing if anything's worth the scroll…", "The scroll says no."))
    }

    // --- Tier 2: second open. A touch of "again? okay." ----------------------

    private val tier2: List<PauseLine> = buildList {
        add(PauseLine.Interference("Back to wrestle the algorithm, round two…"))
        add(PauseLine.Interference("Putting the scroll on hold again already…"))
        add(PauseLine.Interference("Confiscating the slot machine. Again…"))
        add(PauseLine.Interference("Intercepting the second dopamine drop…"))
        add(PauseLine.Interference("The feed missed you for all of ninety seconds…"))
        add(PauseLine.Interference("Re-pausing the thing you just paused…"))
        add(PauseLine.Interference("Reeling the algorithm back in…"))
        add(PauseLine.Interference("Catching round two of the notifications…"))
        add(PauseLine.Interference("Easing your thumb off the refresh, take two…"))
        add(PauseLine.Interference("Telling the for-you page you'll be right with it…"))
        add(PauseLine.Interference("Holding the timeline at arm's length, again…"))
        add(PauseLine.Interference("Politely declining the algorithm's encore…"))
        add(PauseLine.Interference("Slowing the second wind of the scroll…"))
        add(PauseLine.Interference("Putting the dopamine tap back on a slow drip…"))
        add(PauseLine.Interference("Walking the feed back to its corner…"))

        add(PauseLine.TwoBeat("Re-checking for anything new since 90 seconds ago…", "Spoiler: no."))
        add(PauseLine.TwoBeat("Asking the feed to try harder this time…", "It did not."))
        add(PauseLine.TwoBeat("Looking again, really looking…", "Same posts, new order."))
        add(PauseLine.TwoBeat("Giving the algorithm a second chance to impress…", "It blew it."))
        add(PauseLine.TwoBeat("Scanning for the update that justifies a re-open…", "Doesn't exist."))
        add(PauseLine.TwoBeat("Checking if the urgent thing got urgent yet…", "Nope, still fake."))
        add(PauseLine.TwoBeat("Hunting for what changed since last time…", "A number on a badge."))
        add(PauseLine.TwoBeat("Verifying there's a payoff in there somewhere…", "There is not."))
    }

    // --- Tier 3+: third open and beyond. Exasperated at the situation, --------
    //     affectionate toward the user.

    private val tier3: List<PauseLine> = buildList {
        add(PauseLine.Interference("Body-blocking the algorithm one more time, you legend…"))
        add(PauseLine.Interference("Wrestling the slot machine for you again, you magnificent creature…"))
        add(PauseLine.Interference("Prying your thumb off the feed, lovingly…"))
        add(PauseLine.Interference("Going another round with the infinite scroll on your behalf…"))
        add(PauseLine.Interference("The algorithm and I are getting to know each other very well…"))
        add(PauseLine.Interference("Standing guard at the feed's door, shift number infinity…"))
        add(PauseLine.Interference("Confiscating the slot machine for the umpteenth time today…"))
        add(PauseLine.Interference("Holding the line against the timeline, again, for you…"))
        add(PauseLine.Interference("Tackling the dopamine delivery truck once more…"))
        add(PauseLine.Interference("Back on the barricade between you and the for-you page…"))
        add(PauseLine.Interference("Tiring out the algorithm so you don't have to…"))
        add(PauseLine.Interference("Same scroll, same fight, same corner — your ally, reporting in…"))
        add(PauseLine.Interference("Wedging myself between you and the refresh button, again…"))
        add(PauseLine.Interference("The feed is persistent. So am I. Hi again…"))
        add(PauseLine.Interference("Running interference on the slot machine, you absolute trooper…"))

        add(PauseLine.TwoBeat("Surely there's something new this time…", "There is not, friend."))
        add(PauseLine.TwoBeat("Consulting the algorithm one more time…", "It's as empty as ever."))
        add(PauseLine.TwoBeat("Triple-checking for breaking news…", "Breaking: still nothing."))
        add(PauseLine.TwoBeat("Maybe THIS refresh is the one…", "It is the same as the others."))
        add(PauseLine.TwoBeat("Asking what's so important, again…", "Same silence, louder."))
        add(PauseLine.TwoBeat("Looking for the thing. You know the thing…", "It's not here. It never was."))
        add(PauseLine.TwoBeat("Auditing the feed for value, full sweep…", "Audit complete. Value: none."))
        add(PauseLine.TwoBeat("Giving it the benefit of the doubt, last time…", "Doubt confirmed."))
    }
}
