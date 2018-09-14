##################################################################################
##################################################################################
##################################################################################
### Solver built to demonstrate A* algorithm for solving n x m Sliding Puzzles ###
### @author: David S Smith                                                     ###
### @version: 2/10/2018                                                        ###
##################################################################################
##################################################################################
##################################################################################


import heapq #NOTE Python's built in priorityqueue is a minheap. there's no need to cludge the comparison of states to make greater values be considered of lower priority, that is the default
from math import fabs

##################################################################################
##################################################################################
##### STATE OBJECT################################################################
##################################################################################
##################################################################################

class State(object):

    def heuristic(self):
        """
        Returns a heuristic approximation of our distance to a solved puzzle.
        The heuristic here is the manhattan distance for each tile from its intended
        end position.
        """
        y = 0
        x = 0
        approx_distance = 0
        d = 0

        for row in self.list_layout:
            x = 0
            for tile in row:
                if tile != 0:
                    expected_y = (tile - 1)//WIDTH
                    expected_x = (tile - 1)%WIDTH
                    if expected_x < 0: expected_x = 0
                    approx_distance += fabs(expected_x - x) + fabs(expected_y - y)
                else:
                    approx_distance += fabs(WIDTH-1 - x) + fabs(LENGTH-1 - y)
                x+=1
            y+=1

        return approx_distance


    def _id_constr(self):

        string = ""
        for row in self.list_layout:
            for tile in row:
                string+= str(tile) + " "

        return string

    def get_layout(self):
        return self.list_layout

    def get_previous_move(self):
        return self.move_was

    def get_previous(self):
        return self.previous_state

    def set_previous(self, prev):
        self.previous_state = prev

    def get_heur(self):
        return self.heuristic_score

    def __init__(self, list_layout, previous_state, move_was, moves_made):
        """
        Creates a State based on:

        list_layout: A 2d list of integers representing the state's arrangement
        previous_state: Whatever state came before this one (Which may be None)
        move_was: Whatever move brought us to this state, as a String (Which may be the empty string)
        moves_made: However many moves have been made to reach this state
        """
        #inputted vars
        self.list_layout = list_layout
        self.previous_state = previous_state
        self.move_was = move_was
        self.moves_made = moves_made
        #calculated vars
        self.heuristic_score = self.heuristic()
        self.id_str = self._id_constr()

    def __gt__(self, other):
        return (self.heuristic_score + self.moves_made) > (other.heuristic_score + other.moves_made)

    def __lt__(self, other):
        return (self.heuristic_score + self.moves_made) < (other.heuristic_score + other.moves_made)

    def __eq__(self, other):
        return self.id_str == other.id_str

    def __hash__(self):
        return self.id_str.__hash__()

    def __str__(self):
        """Constructs a string to represent a State, based on its layout"""
        string = ""
        for row in self.list_layout:
            for tile in row:
                string+= str(tile) + ", "
            string+= "\n"
        return string



##################################################################################

WIDTH = 0
LENGTH = 0

##################################################################################
##################################################################################
#### SOLVE FUNCTION ##############################################################
##################################################################################
##################################################################################
def solve(puzzle):
    """
    Solves an m x n sliding puzzle (inputted as a 2d list of integers with 0 as the empty slot) in as few moves as possible, returning the solution
    as a list of strings following this convention:

        U: Move a tile up into the empty slot
        D: Move a tile down into the empty slot
        L: Move a tile left into the empty slot
        R: Move a tile right into the empty slot

    If the puzzle is unsolveable, this function returns None.
    """
    #define w x l
    global WIDTH
    WIDTH = len(puzzle[0])
    global LENGTH
    LENGTH = len(puzzle)

    if not _solveable(puzzle): #solveability test
        return None

    current = State(puzzle, None, "", 0) #construct the start

    open_list = []
    closed_list = set()
    num_moves = -1

    heapq.heappush(open_list, current)
    approximation_to_goal = current.get_heur()

    while (approximation_to_goal != 0): #until we are 0 distance from the goal (at the goal)

        current = heapq.heappop(open_list)#poll the queue

        if current in closed_list: continue

        approximation_to_goal = current.get_heur()
        next_options = _neighbors(current)

        for option in next_options: #for each possible move
            if option not in closed_list: #if it's not a step back to a previous state
                heapq.heappush(open_list, option) #add it

        closed_list.add(current)
        num_moves+=1

    #Congrats, we reached the goal!
    solution = []

    while current.get_previous() is not None: #while we have stuff to look back to
        solution.insert(0, current.get_previous_move())
        current = current.get_previous()

    return solution





##################################################################################

def _find_zero(puzzle):
    """Returns the coordinates of 0 as a tuple of (row,col)"""
    y = 0
    x = 0

    for row in puzzle:
        x = 0
        for tile in row:
            if tile == 0:
                return (y,x)
            x+=1
        y+=1

    return None

##################################################################################

def _solveable(puzzle):
    """Returns True for a puzzle in the solveable orbit, false otherwise."""
    flat_list = sum(puzzle, []) #flatten list,
    flat_list.remove(0) #remove 0

    i = 0
    j = 0
    flat_length = len(flat_list)
    num_inversions = 0


    for i in range(0, flat_length): #from the start to the end of the list
        for j in range(i, flat_length): #from a given index to the end
            if(flat_list[j] < flat_list[i]):
                num_inversions+=1 #track number of inversions

    if (WIDTH % 2 != 0): #if the width is odd
        if(num_inversions % 2 == 0): #and the number of inversions is even
            return True #puzzle is solveable
        else:
            return False
    else:
        empty = _find_zero(puzzle)
        distance_empty_moves = fabs(empty[0] - len(puzzle) - 1)
        check = distance_empty_moves + num_inversions
        if(check % 2 == 0):
            return True
        else:
            return False

##################################################################################

def _neighbors(center):
    """
    Returns a list containing possible moves as States referencing a given State
    for the number of moves to reach them, their previous state, and what moves
    they can be.

    Moves that would involve non-existent tiles off the board are not returned, meaning
    the list length varies.
    """
    moves = []

    #UP
    up_state = _move_up(center)
    if up_state is not None: moves.append(up_state)

    #DOWN
    down_state = _move_down(center)
    if down_state is not None: moves.append(down_state)

    #LEFT
    left_state = _move_left(center)
    if left_state is not None: moves.append(left_state)

    #RIGHT
    right_state = _move_right(center)
    if right_state is not None: moves.append(right_state)

    return moves



def _move_up(center):
    """Returns a State based on taking the inputted state and moving a tile up to occupy the empty slot"""
    empty = _find_zero(center.list_layout)

    temp_y = empty[0] + 1
    temp_x = empty[1]

    if temp_y <= LENGTH-1:
        up = [row[:] for row in center.get_layout()]
        _swap(up, empty[0], empty[1], temp_y, temp_x)
        up_state = State(up, center, "U", (center.moves_made+1))
        return up_state
    else:
        return None



def _move_down(center):
    """Returns a State based on taking the inputted state and moving a tile down to occupy the empty slot"""
    empty = _find_zero(center.list_layout)

    temp_y = empty[0] - 1 #one above is one less on y
    temp_x = empty[1]

    if temp_y >= 0:
        down = [row[:] for row in center.get_layout()]
        _swap(down, empty[0], empty[1], temp_y, temp_x)
        down_state = State(down, center, "D", (center.moves_made+1))
        return down_state
    else:
        return None



def _move_left(center):
    """Returns a State based on taking the inputted state and moving a tile left to occupy the empty slot"""
    empty = _find_zero(center.list_layout)

    temp_y = empty[0]
    temp_x = empty[1] + 1

    if temp_x <= WIDTH-1:
        left = [row[:] for row in center.get_layout()]
        _swap(left, empty[0], empty[1], temp_y, temp_x)
        left_state = State(left, center, "L", (center.moves_made+1))
        return left_state
    else:
        return None



def _move_right(center):
    """Returns a State based on taking the inputted state and moving a tile right to occupy the empty slot"""
    empty = _find_zero(center.list_layout)

    temp_y = empty[0]
    temp_x = empty[1] - 1

    if temp_x >= 0:
        right = [row[:] for row in center.get_layout()]
        _swap(right, empty[0], empty[1], temp_y, temp_x)
        right_state = State(right, center, "R", (center.moves_made+1))
        return right_state
    else:
        return None



def _swap(puzzle, y1, x1, y2, x2):
    """
    Off a given 2d list, swaps two elements based off a pair of coords.
    One element MUST BE ZERO.
    """
    assert puzzle[y1][x1] == 0 or puzzle[y2][x2] == 0
    temp = puzzle[y1][x1]
    puzzle[y1][x1] = puzzle[y2][x2]
    puzzle[y2][x2] = temp
    return puzzle
