package com.spacecar.imyself.logic

object FibonacciProgressor {
    private val MILESTONES = listOf(1, 2, 3, 5, 8, 13, 21, 34, 55, 89, 144, 233, 377, 610, 987)

    fun getNextMilestone(currentMilestone: Int): Int {
        val index = MILESTONES.indexOf(currentMilestone)
        if (index == -1 || index == MILESTONES.lastIndex) {
            // If somehow not found, or at the end of the list, just add the previous two (standard Fib)
            if (index == MILESTONES.lastIndex) {
                return MILESTONES[index] + MILESTONES[index - 1]
            }
            return 1 // Fallback to start
        }
        return MILESTONES[index + 1]
    }
    
    fun getInitialMilestone(): Int = MILESTONES.first()
}
