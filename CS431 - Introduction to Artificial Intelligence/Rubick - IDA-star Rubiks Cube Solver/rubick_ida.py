from rubick_cube_blocks import *
import sys

"""
The IDA* search of Cube searchspace. Tries to find a path from a starting state to a solved cube.
"""




path = [] #path, to be treated as a stack

def ida_star(start):
    """
    Begins the IDA* search from a given start Cube. Returns a pair containing the path to find the goal and the the number of moves to find it.
    If the state is for some reason unreachable, returns None.
    """

    bound = start.heuristic()

    path.append(start)

    while(True):
        t = search(path, 0, bound)

        if t == FOUND:

            return (path, bound)

        if t == sys.maxsize:
            return None
        bound = t


FOUND = -1 #indicator

def search(path, g, bound):
    """
    Starts searching from the head of this path, having traveled a distance of g, with a maximum bound
    Returns a number indicating if the result has been found.
    """


    node = path[len(path)-1] #NOTE: the end of the stack but not popped off
    node.heuristic()
    node.update_f(g)
    f = node.f_dist
    if f > bound: return f
    if is_solved(node):
        return FOUND

    min = sys.maxsize
    successors = node.produce_moves(g+1)

    successors.sort()
    for succ in successors:

        if succ not in path:
            path.append(succ)
            t = search(path, (g+1), bound)
            if t == FOUND: return FOUND
            if t < min: min = t

            path.pop()

    return min

def is_solved(cube):
    """
    Checks for equality with the solved cube
    """
    return cube == solved_cube
