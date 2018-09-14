"""
Minimax Connect Four player of variable depth
"""
__author__ = "David S Smith"
__date__ = "February 22, 2018"



our_player = -1
class ComputerPlayer:
    def __init__(self, player_id, difficulty_level):
        """
        Constructor, takes a difficulty level (likely the # of plies to look
        ahead), and a player ID that's either 1 or 2 that tells the player what
        its number is.
        """
        self.player_id = player_id
        self.difficulty_level = difficulty_level
        global our_player
        our_player = player_id

    def _flip_player(self, player_number):
        if player_number == 1: return 2
        else: return 1

    def pick_move(self, rack):
        """
        Move picking function that takes the rack as a 2d list, in column row order, starting from the bottom left
        Returns a number representing the index of the column to play in, numbered 0-the width of the board
        """

        global WIDTH

        WIDTH = len(rack)

        list_rack = [list(slot) for slot in rack] #untuple-ize
        current = State(list_rack, self.player_id) #make state of current rack

        options = []
        #look at all options at first ply layer, which will have recursed down to the bottom
        for c in range(WIDTH): #for every column in the board

            #make a state based on the move at column c
            attempt = current.simul_move(c, self.player_id)

            if attempt is not None: #if this produced a move
                #next player gets to move
                if self.player_id == 1: next_player = 2
                else: next_player = 1
                attempt_score = self._find_move(attempt, 1, self.difficulty_level, next_player)

                #put this move and its column into a list
                options.append((attempt_score, c))

        #pick the maximum
        selected = max(options)
        #pick move associated with maximum
        return selected[1]





    def _find_move(self, current, ply, difficulty_level, player):
        """
            Recursive move finding function, takes the current state of the board as a State,
            the current depth of recursion, the difficulty level, (where we'll stop recursing)
            and the current player who's next move we're simulating (1 or 2)
        """

        #check the score of this state
        node_score = current.get_score()
        #if this state is a win for either side, or if we're at the end of our difficulty depth level
        if ply == difficulty_level or node_score > 100000000 or node_score < -100000000: #base case
            return node_score * (difficulty_level+1-ply) #NOTE: I'm not sure if this is paranoid, but I want to make sure it doesn't multiply by 0 if we hit a win condition at the end of our search

        #recursive
        else:
            options = []
            #we're either player one (Min) or player two (Max)

            #for a column at c in the rack
            for c in range(WIDTH):

                #simulate a move in that column, making an attempt State
                attempt = current.simul_move(c, player)

                if attempt is not None: #if this produced a move
                    if player == 1: next_player = 2
                    else: next_player = 1

                    #recurse down this attempted move
                    attempt_score = self._find_move(attempt, ply+1, difficulty_level, next_player)
                    #add the results of each column move into options
                    options.append(attempt_score)
            if len(options) == 0: return 0
            #based on whether we're the current player or not, max (if we are) or min (if we aren't) and pass back the result
            if player == self.player_id: return max(options)
            else: return min(options)








class State(object):


    def __init__(self, layout, player):
        """
        Takes the layout of the rack as a 2d list, column row order, and the
        player who has played to make this state
        """
        self.layout = [x[:] for x in layout] #this state's layout is a copy
        self.height = len(layout[0])
        self.width = len(layout)
        self.who_played = player
        self.score = self._scoring() #score for this board


    def __str__(self):
        """
        Constructs a string to represent a State, based on its layout
        NOTE: BOARD IS ROTATED 90 DEGREES CLOCKWISE. FOR DEBUGGING ONLY
        """
        string = ""
        for row in self.layout:
            for tile in row:
                string+= str(tile) + " "
            string+= "\n"
        return string

    def get_score(self):
        """
        Returns the score of the State
        """
        return self.score


    def simul_move(self, column, player):
        """
        Simulates a move in a given column by inputted player 1 or 2, returning
        a state with that move
        """

        new_rack = [x[:] for x in self.layout] #copy this state's layout
        #travel down this column
        r = 0
        while new_rack[column][r] !=0:
            r+=1
            if r >= self.height:
                return None
        new_rack[column][r] = player
        new_state = State(new_rack, player)
        return new_state



    # o     o     o
    #   o   o   o
    #     o o o
    #       o o o o  This is the shape of what directions we look out from a given [c][r] coord
    def _scoring(self):
        """
        Scores a State based off of score values derived from each possible quartet in the rack
        """
        val = 0 #score will be totaled here

        for c in range(0, self.width): #for every column in the board
            for r in range(0, self.height): #for every row of a column
                #see if we can move...
                possible_up = (r + 3 < self.height) #up?
                possible_left = (c - 3 > 0) #left?
                possible_right = (c + 3 < self.width) #right?

                #diagonally up, left
                if possible_up and possible_left:
                    val+= self._up_left(c, r)

                #up
                if possible_up:
                    val+= self._up(c,r)

                #diagonally up, right
                if possible_up and possible_right:
                    val+= self._up_right(c,r)

                #right
                if possible_right:
                    val+= self._right(c,r)


        return val



    #RETURN A SCORE OF THIS QUARTET
    def _up_left(self, col, row):
        """
        Counts the number of ones and twos in a quartet moving up and left from index [col][row]
        Returns a score based off this.
        """
        ones = 0
        twos = 0
        for step in range(4):

            current = self.layout[col + (step*-1)][row + (step)] #step up and left
            if current == 1: ones+=1
            if current == 2: twos+=1

        return self._score_a_quartet(ones, twos)

    def _up(self, col, row):
        """
        Counts the number of ones and twos in a quartet moving up from index [col][row]
        Returns a score based off this.
        """
        ones = 0
        twos = 0
        for step in range(4):
            current = self.layout[col][row + (step)] #step up
            if current == 1: ones+=1
            if current == 2: twos+=1

        return self._score_a_quartet(ones, twos)

    def _up_right(self, col, row):
        """
        Counts the number of ones and twos in a quartet moving up and right from index [col][row]
        Returns a score based off this.
        """
        ones = 0
        twos = 0
        for step in range(4):
            current = self.layout[col + (step)][row + (step)] #step up and right
            if current == 1: ones+=1
            if current == 2: twos+=1

        return self._score_a_quartet(ones, twos)

    def _right(self, col, row):
        """
        Counts the number of ones and twos in a quartet moving right from index [col][row]
        Returns a score based off this.
        """
        ones = 0
        twos = 0
        for step in range(4):
            current = self.layout[col + (step)][row] #step and right
            if current == 1: ones+=1
            if current == 2: twos+=1

        return self._score_a_quartet(ones, twos)


    def _score_a_quartet(self, num_one, num_two):
        """Based off an inputted number of ones and twos, returns the score for a quartet"""
        score = 0
        if num_one > 0 and num_two > 0: return 0 #no one can win here, or nothing is here yet
        elif num_one == 0 and num_two == 0: return 0

        elif num_two == 4 or num_one == 4: score = 100000000 #someone wins

        elif num_two == 3 or num_one == 3: score = 100

        elif num_two == 2 or num_one == 2: score = 10

        elif num_two == 1 or num_one == 1: score = 1

        else: #This should never happen
            print("That's not right. There are " + str(num_one) + " ones and " + str(num_two) + " twos here.")
            return None

        if self.who_played != our_player: return score * -1
        return score
