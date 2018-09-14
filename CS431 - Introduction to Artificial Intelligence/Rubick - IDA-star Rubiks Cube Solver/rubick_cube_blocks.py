import copy
import math

class Block():
    def __init__(self, front=None, top=None, opposite=None, bottom=None, left=None, right=None):
        """
        Each Block (or cubie) contains either 2 or 3 faces with colour values represented as single capital letters
        (which should match up to the first letter of the colour). All other values will be None.
        """
        self.front = front
        self.top = top
        self.opposite = opposite
        self.bottom = bottom
        self.left = left
        self.right = right

        l = [front,top,opposite,bottom,left,right]

        #A block's id determines the colours on the block, represented as true/false values
        #These colours are given in this order: (Red, Green, Orange, Blue, Yellow, White)
        l = [
        (l.__contains__("R")),
        (l.__contains__("G")),
        (l.__contains__("O")),
        (l.__contains__("B")),
        (l.__contains__("Y")),
        (l.__contains__("W"))
        ]

        self.blk_id = tuple(l)

        #if only 3 of the above fields have values, it is a corner
        #if only 2 of the above fields have values, it is an edge
        #if it has more fields with values than 3, it is an invalid configuration

    def re_eval_id(self):
        """
        Blocks some times get assigned new values without their ids necessarily updating.
        In this case, this function updates the id of the block. Block ids represent
        the colours present on the block (which will be unique for each block on the cube),
        without containing information on orientation.
        """
        l = [self.front, self.top, self.opposite, self.bottom, self.left, self.right]

        #A block's id determines the colours on the block, represented as true/false values
        #These colours are given in this order: (Red, Green, Orange, Blue, Yellow, White)
        l = [
        (l.__contains__("R")),
        (l.__contains__("G")),
        (l.__contains__("O")),
        (l.__contains__("B")),
        (l.__contains__("Y")),
        (l.__contains__("W"))
        ]

        self.blk_id = tuple(l)

    def __eq__(self, other):
        """
        Blocks are equal if they have the same colours on their faces. (orientation is not a factor)
        """
        return self.blk_id == other.blk_id

    def __hash__(self):
        """
        Hashcodes the block id. As such, blocks of different orientation map to the same hash.
        """
        return self.blk_id.__hash__()

    def f_to_t(self):
        """
        Reassigns values to represent the block turning 90 degrees upwards (front to top)
        """
        temp = self.front
        self.front = self.bottom
        self.bottom = self.opposite
        self.opposite = self.top
        self.top = temp

    def o_to_t(self):
        """
        Reassigns values to represent the block turning 90 degrees downwards (opposite to top)
        """
        temp = self.front
        self.front = self.top
        self.top = self.opposite
        self.opposite = self.bottom
        self.bottom = temp

    def l_to_t(self):
        """
        Reassigns values to represent the block turning 90 degrees counter-clockwise on the front-back axis (left to top)
        """
        temp = self.left
        self.left = self.bottom
        self.bottom = self.right
        self.right = self.top
        self.top = temp

    def r_to_t(self):
        """
        Reassigns values to represent the block turning 90 degrees clockwise on the front-back axis (right to top)
        """
        temp = self.right
        self.right = self.bottom
        self.bottom = self.left
        self.left = self.top
        self.top = temp

    def r_to_o(self):
        """
        Reassigns values to represent the block turning 90 degrees counter-clockwise on the top-bottom axis (right to opposite)
        """
        temp = self.front
        self.front = self.left
        self.left = self.opposite
        self.opposite = self.right
        self.right = temp

    def l_to_o(self):
        """
        Reassigns values to represent the block turning 90 degrees clockwise on the top-bottom axis (left to opposite)
        """
        temp = self.front
        self.front = self.right
        self.right = self.opposite
        self.opposite = self.left
        self.left = temp

    def __str__(self):
        """
        Prints each face of a block.
        """
        string = ""
        string += "front: " + str(self.front)
        string += "\ntop: " + str(self.top)
        string += "\nopposite: " + str(self.opposite)
        string += "\nbottom: " + str(self.bottom)
        string += "\nleft: " + str(self.left)
        string += "\nright: " + str(self.right)
        return string

#if only 1 of the above fields have values, it is a pivot
class Pivot():

    def __init__(self, colour):
        """
        A Pivot is a simplified version of a Block, containing its colour. There are 7 pivots in the cube,
        One for each face, and one in the center with some non-value assigned to it (such as None or "")
        """
        self.colour = colour
        self.blk_id = colour

    def __str__(self):
        return self.colour







class Cube():
    def __init__(self, front_list=[], top_list=[], opposite_list=[], bottom_list=[], left_list=[], right_list=[]):
        """
        Cubes take in lists representing the read-ins of each face (see rubick.py documentation for more details)
        """
        self.layout = []
        self.previous_move = "start"

        #CONSTRUCTING THE FRONT LAYER
        #FRONT PIVOT IS "RED"
        #ORIENTED SUCH THAT ABOVE RED WILL BE GREEN, TO THE LEFT WILL BE YELLOW, TO THE RIGHT WILL BE WHITE, AND THE BOTTOM WILL BE BLUE
        if(len(front_list) !=0):
            first_layer = [
                [
                    Block(front = front_list[0], top = top_list[6], left = left_list[2]),
                    Block(front = front_list[1], top = top_list[7]),
                    Block(front = front_list[2], top = top_list[8], right = right_list[0])
                ]
                ,
                [
                    Block(front = front_list[3], left = left_list[5]),
                    Pivot("R"),
                    Block(front = front_list[5], right = right_list[3])
                ]
                ,
                [
                    Block(front = front_list[6], left = left_list[8], bottom = bottom_list[0]),
                    Block(front = front_list[7], bottom = bottom_list[1]),
                    Block(front = front_list[8], right = right_list[6], bottom = bottom_list[2])
                ]
            ]

            #CONSTRUCTING MIDDLE LAYER
            #THIS CONTAINS ONLY EDGES AND PIVOTS

            middle_layer = [
                [
                    Block(top = top_list[3], left = left_list[1]),
                    Pivot("G"),
                    Block(top = top_list[5], right = right_list[1])
                ]
                ,
                [
                    Pivot("Y"),
                    Pivot(""), #this is the core of the cube. We treat it as a pivot so it will be ignored
                    Pivot("W")
                ]
                ,
                [
                    Block(left = left_list[7], bottom = bottom_list[3]),
                    Pivot("B"),
                    Block(right = right_list[7], bottom = bottom_list[5])
                ]
            ]

            #CONSTRUCTING OPPOSITE LAYER
            #OPPOSITE PIVOT IS ORANGE
            #NOTE: The instructions for inputting cube tiles result in the side being upside down.
            #      As such, the top row has the last few tiles on the face from the list input. This is intended.
            back_layer = [
                [
                    Block(opposite = opposite_list[6], top = top_list[0], left = left_list[0]),
                    Block(opposite = opposite_list[7], top = top_list[1]),
                    Block(opposite = opposite_list[8], top = top_list[2], right = right_list[2])
                ]
                ,
                [
                    Block(opposite = opposite_list[3], left = left_list[3]),
                    Pivot("O"),
                    Block(opposite = opposite_list[5], right = right_list[5])
                ]
                ,
                [
                    Block(opposite = opposite_list[0], left = left_list[6], bottom = bottom_list[6]),
                    Block(opposite = opposite_list[1], bottom = bottom_list[7]),
                    Block(opposite = opposite_list[2], right = right_list[8], bottom = bottom_list[8])
                ]
            ]

            self.layout = [first_layer, middle_layer, back_layer]
        self.cube_id = self.construct_id() #id is a tuple of the block ids, to be used in hash function
        self.heuristic_score = None
        self.f_dist = None

    def __str__(self):
        """
        Printable string for debug purposes.
        """
        string = "CUBE #####\n"
        for l in range(len(self.layout)):
            layer = self.layout[l]
            for r in range(len(layer)):
                row = layer[r]
                for b in range(len(row)):
                    block = self.layout[l][r][b]
                    string+= str(block) + "\n\n"
        return string

    def update_f(self, distance_to_reach):
        """
        Takes in a distance to reach this Cube state and adds it to the heuristic estimate
        to update "f", representing an estimate score for the cube.
        """
        self.f_dist = distance_to_reach + self.heuristic_score

    def __hash__(self):
        return self.cube_id.__hash__()

    def __gt__(self, other):
        """
        A cube is greater than another cube if it's f_dist score estimate is greater
        """
        return self.f_dist > other.f_dist

    def __lt__(self, other):
        """
        A cube is lesser than another cube if it's f_dist score estimate is lesser
        """
        return self.f_dist < other.f_dist

    def __eq__(self, other):
        """
        Two cubes are equal if every block is in the same position for each cube.
        """
        for l in range(len(self.layout)):
            layer = self.layout[l]
            for r in range(len(layer)):
                row = layer[r]
                for b in range(len(row)):
                    block = self.layout[l][r][b]
                    other_block = other.layout[l][r][b]
                    if type(block) is not Pivot:
                        if(block.blk_id != other_block.blk_id):
                            return False
        return True


    def construct_id(self):
        """
        A cube's id is indicative of the id of each of its blocks in its position. As such, cubes
        of the same id will have the same cubes in places. Technically, this could cause problems
        since block orientation isn't taken into account, but presumably legal cubes won't cause
        any issues (and cubes are limited to facing outwards in terms of orientation).
        """
        hash_list = []
        for layer in self.layout:
            for row in layer:
                for block in row:
                    if type(block) is not Pivot:
                        block.re_eval_id()
                        hash_list.append(block.blk_id)

        hash_tuple = tuple(hash_list)
        return hash_tuple


    def produce_moves(self, distance_to_reach):
        """
        Generates a list of cubes that are all possible quarter turn movements from this cube's state.
        """


        top_cw = self.top_down_axis_cw(0)
        top_cw_cube = Cube()
        top_cw_cube.layout = top_cw
        top_cw_cube.previous_move = "Rotate the top face clockwise"

        top_ccw = self.top_down_axis_ccw(0)
        top_ccw_cube = Cube()
        top_ccw_cube.layout = top_ccw
        top_ccw_cube.previous_move = "Rotate the top face counter-clockwise"


        bottom_cw = self.top_down_axis_cw(2)
        bottom_cw_cube = Cube()
        bottom_cw_cube.layout = bottom_cw
        bottom_cw_cube.previous_move = "Rotate the bottom face clockwise (with respect to the top)"

        bottom_ccw = self.top_down_axis_ccw(2)
        bottom_ccw_cube = Cube()
        bottom_ccw_cube.layout = bottom_ccw
        bottom_ccw_cube.previous_move = "Rotate the bottom face counter-clockwise (with respect to the top)"


        left_up = self.left_right_axis_upwards(0)
        left_up_cube = Cube()
        left_up_cube.layout = left_up
        left_up_cube.previous_move = "Rotate the left face's section upwards"

        left_down = self.left_right_axis_downwards(0)
        left_down_cube = Cube()
        left_down_cube.layout = left_down
        left_down_cube.previous_move = "Rotate the left face's section downwards"


        right_up = self.left_right_axis_upwards(2)
        right_up_cube = Cube()
        right_up_cube.layout = right_up
        right_up_cube.previous_move = "Rotate the right face's section upwards"

        right_down = self.left_right_axis_downwards(2)
        right_down_cube = Cube()
        right_down_cube.layout = right_down
        right_down_cube.previous_move = "Rotate the right face's section downwards"


        front_cw = self.front_opp_axis_cw(0)
        front_cw_cube = Cube()
        front_cw_cube.layout = front_cw
        front_cw_cube.previous_move = "Rotate the front face clockwise"

        front_ccw = self.front_opp_axis_ccw(0)
        front_ccw_cube = Cube()
        front_ccw_cube.layout = front_ccw
        front_ccw_cube.previous_move = "Rotate the front face counter-clockwise"


        back_cw = self.front_opp_axis_cw(2)
        back_cw_cube = Cube()
        back_cw_cube.layout = back_cw
        back_cw_cube.previous_move = "Rotate the back face clockwise (with respect to the front)"

        back_ccw = self.front_opp_axis_ccw(2)
        back_ccw_cube = Cube()
        back_ccw_cube.layout = back_ccw
        back_ccw_cube.previous_move = "Rotate the back face counter-clockwise (with respect to the front)"


        cubes = [top_cw_cube, top_ccw_cube, bottom_cw_cube, bottom_ccw_cube, left_up_cube, left_down_cube, right_up_cube, right_down_cube, front_cw_cube, front_ccw_cube, back_cw_cube, back_ccw_cube]
        for c in cubes:
            c.construct_id() #fix ids
            c.heuristic()
            c.update_f(distance_to_reach)
        return cubes



    #MOVE FUNCTIONS

    def front_opp_axis_cw(self, layer): #used for what officially is considered an f turn and b turn. clockwise rotations about front or opposite face
        """
        Rotates a face along the front-opposite axis clockwise based off a given layer (0 for front, 2 for opposite, 1 invalid)
        """
        new_cube_layout = copy.deepcopy(self.layout)
        to_rotate = new_cube_layout[layer]

        #Rotate corners
        top_right = to_rotate[0][2] #store top right corner
        to_rotate[0][2] = to_rotate[0][0] #top left to top right
        to_rotate[0][0] = to_rotate[2][0] #bot left to top left
        to_rotate[2][0] = to_rotate[2][2]#bot right for bot left
        to_rotate[2][2] = top_right #top right to bottom right
        to_rotate[0][0].l_to_t()
        to_rotate[2][0].l_to_t()
        to_rotate[0][2].l_to_t()
        to_rotate[2][2].l_to_t()

        #rotate edges
        top = to_rotate[0][1] #store top
        to_rotate[0][1] = to_rotate[1][0] #left to top
        to_rotate[1][0] = to_rotate[2][1] #bot to left
        to_rotate[2][1] = to_rotate[1][2] #right to bot
        to_rotate[1][2] = top

        to_rotate[0][1].l_to_t()
        to_rotate[1][0].l_to_t()
        to_rotate[1][2].l_to_t()
        to_rotate[2][1].l_to_t()

        new_cube_layout[layer] = to_rotate

        return new_cube_layout




    def front_opp_axis_ccw(self, layer): #also known as a F inv turn
        """
        Rotates a face along the front-opposite axis counter-clockwise based off a given layer (0 for front, 2 for opposite, 1 invalid)
        """
        new_cube_layout = copy.deepcopy(self.layout)
        to_rotate = new_cube_layout[layer]

        #Rotate corners
        top_right = to_rotate[0][2] #store top right corner
        to_rotate[0][2] = to_rotate[2][2] #bot right to top right
        to_rotate[2][2] = to_rotate[2][0] #bot left to bot right
        to_rotate[2][0] = to_rotate[0][0]#bot right for bot left
        to_rotate[0][0] = top_right #top right to bottom right
        to_rotate[0][0].r_to_t()
        to_rotate[2][0].r_to_t()
        to_rotate[0][2].r_to_t()
        to_rotate[2][2].r_to_t()

        #rotate edges
        top = to_rotate[0][1] #store top
        to_rotate[0][1] = to_rotate[1][2] #right to top
        to_rotate[1][2] = to_rotate[2][1] #bot to right
        to_rotate[2][1] = to_rotate[1][0] #left to bot
        to_rotate[1][0] = top

        to_rotate[0][1].r_to_t()
        to_rotate[1][0].r_to_t()
        to_rotate[1][2].r_to_t()
        to_rotate[2][1].r_to_t()

        new_cube_layout[layer] = to_rotate

        return new_cube_layout

    def top_down_axis_cw(self, layer):
        """
        Rotates a face along the top-bottom axis clockwise based off a given layer (0 for top, 2 for bottom, 1 invalid)
        """
        new_cube_layout = copy.deepcopy(self.layout)

        #Rotate Corners
        front_left = new_cube_layout[0][layer][0]
        new_cube_layout[0][layer][0] = new_cube_layout[0][layer][2]
        new_cube_layout[0][layer][2] = new_cube_layout[2][layer][2]
        new_cube_layout[2][layer][2] = new_cube_layout[2][layer][0]
        new_cube_layout[2][layer][0] = front_left

        new_cube_layout[0][layer][0].l_to_o()
        new_cube_layout[0][layer][2].l_to_o()
        new_cube_layout[2][layer][2].l_to_o()
        new_cube_layout[2][layer][0].l_to_o()

        #Rotate Edges
        front_mid = new_cube_layout[0][layer][1]
        new_cube_layout[0][layer][1] = new_cube_layout[1][layer][2]
        new_cube_layout[1][layer][2] = new_cube_layout[2][layer][1]
        new_cube_layout[2][layer][1] = new_cube_layout[1][layer][0]
        new_cube_layout[1][layer][0] = front_mid

        new_cube_layout[1][layer][2].l_to_o()
        new_cube_layout[0][layer][1].l_to_o()
        new_cube_layout[2][layer][1].l_to_o()
        new_cube_layout[1][layer][0].l_to_o()

        return new_cube_layout

    def top_down_axis_ccw(self, layer): #layer = 0 for top, 2 for bottom
        """
        Rotates a face along the top-bottom axis counter-clockwise based off a given layer (0 for top, 2 for bottom, 1 invalid)
        """
        new_cube_layout = copy.deepcopy(self.layout)

        #Rotate Corners
        front_left = new_cube_layout[0][layer][0]
        new_cube_layout[0][layer][0] = new_cube_layout[2][layer][0]
        new_cube_layout[2][layer][0] = new_cube_layout[2][layer][2]
        new_cube_layout[2][layer][2] = new_cube_layout[0][layer][2]
        new_cube_layout[0][layer][2] = front_left

        new_cube_layout[0][layer][0].r_to_o()
        new_cube_layout[0][layer][2].r_to_o()
        new_cube_layout[2][layer][2].r_to_o()
        new_cube_layout[2][layer][0].r_to_o()

        #Rotate Edges
        front_mid = new_cube_layout[0][layer][1]
        new_cube_layout[0][layer][1] = new_cube_layout[1][layer][0]
        new_cube_layout[1][layer][0] = new_cube_layout[2][layer][1]
        new_cube_layout[2][layer][1] = new_cube_layout[1][layer][2]
        new_cube_layout[1][layer][2] = front_mid

        new_cube_layout[0][layer][1].r_to_o()
        new_cube_layout[1][layer][0].r_to_o()
        new_cube_layout[2][layer][1].r_to_o()
        new_cube_layout[1][layer][2].r_to_o()

        return new_cube_layout

    def left_right_axis_upwards(self, layer):
        """
        Rotates a face along the left-right axis upwards based off a given layer (0 for left, 2 for right, 1 invalid)
        """
        new_cube_layout = copy.deepcopy(self.layout)

        #Rotate Corners
        front_top = new_cube_layout[0][0][layer]
        new_cube_layout[0][0][layer] = new_cube_layout[0][2][layer]
        new_cube_layout[0][2][layer] = new_cube_layout[2][2][layer]
        new_cube_layout[2][2][layer] = new_cube_layout[2][0][layer]
        new_cube_layout[2][0][layer] = front_top

        new_cube_layout[0][0][layer].f_to_t()
        new_cube_layout[0][2][layer].f_to_t()
        new_cube_layout[2][2][layer].f_to_t()
        new_cube_layout[2][0][layer].f_to_t()

        #Rotate Edge
        front_mid = new_cube_layout[0][1][layer]
        new_cube_layout[0][1][layer] = new_cube_layout[1][2][layer]
        new_cube_layout[1][2][layer] = new_cube_layout[2][1][layer]
        new_cube_layout[2][1][layer] = new_cube_layout[1][0][layer]
        new_cube_layout[1][0][layer] = front_mid

        new_cube_layout[0][1][layer].f_to_t()
        new_cube_layout[1][2][layer].f_to_t()
        new_cube_layout[2][1][layer].f_to_t()
        new_cube_layout[1][0][layer].f_to_t()

        return new_cube_layout


    def left_right_axis_downwards(self, layer):
        """
        Rotates a face along the left-right axis downwards based off a given layer (0 for left, 2 for right, 1 invalid)
        """
        new_cube_layout = copy.deepcopy(self.layout)

        #Rotate Corners
        front_top = new_cube_layout[0][0][layer]
        new_cube_layout[0][0][layer] = new_cube_layout[2][0][layer]
        new_cube_layout[2][0][layer] = new_cube_layout[2][2][layer]
        new_cube_layout[2][2][layer] = new_cube_layout[0][2][layer]
        new_cube_layout[0][2][layer] = front_top

        new_cube_layout[0][0][layer].o_to_t()
        new_cube_layout[0][2][layer].o_to_t()
        new_cube_layout[2][2][layer].o_to_t()
        new_cube_layout[2][0][layer].o_to_t()

        #Rotate Edge
        front_mid = new_cube_layout[0][1][layer]
        new_cube_layout[0][1][layer] = new_cube_layout[1][0][layer]
        new_cube_layout[1][0][layer] = new_cube_layout[2][1][layer]
        new_cube_layout[2][1][layer] = new_cube_layout[1][2][layer]
        new_cube_layout[1][2][layer] = front_mid

        new_cube_layout[1][2][layer].o_to_t()
        new_cube_layout[0][1][layer].o_to_t()
        new_cube_layout[2][1][layer].o_to_t()
        new_cube_layout[1][0][layer].o_to_t()

        return new_cube_layout

    def heuristic(self):
        """
        The heuristic for a cube's proximity to the goal state is the manhattan_distance of each block
        from it's final position divided by 8.
        """
        #to calculate the heuristic score of a cube

        heuristic = 0
        #take each cube in the layout
        for l in range(len(self.layout)):
            layer = self.layout[l]

            for r in range(len(layer)):
                row = self.layout[l][r]

                for b in range(len(row)):

                    block_current = self.layout[l][r][b]
                    #skip pivots, they can't move
                    if(type(block_current) is not Pivot):
                        #calculate the manhattan distance in 3dspace between its current position and its intended position
                        dist = self.manhattan_3D(block_current, l, r, b)
                        #add it to a total distance
                        heuristic+=dist

        #divide it by 8
        heuristic=(heuristic/8)

        self.heuristic_score = heuristic
        return heuristic


    def manhattan_3D(self, block, curr_lay, curr_row, curr_blk):
        """Finds the manhattan of a block to it's final position. Does not take orientation into account"""
        id = block.blk_id
        intended_coords = solution_position[id] #find where this cube would be in a solved cube

        intend_lay = intended_coords[0]
        intend_row = intended_coords[1]
        intend_blk = intended_coords[2]

        #md3d(p1, p2) = |x1−x2|+|y1−y2|+|z1−z2|
        manhattan_distance = abs(intend_lay - curr_lay) + abs(intend_row - curr_row) + abs(intend_blk - curr_blk)

        return manhattan_distance/2




#DEFAULT CUBE

solved_cube = Cube(
front_list    = "RRRRRRRRR",
top_list      = "GGGGGGGGG",
opposite_list = "OOOOOOOOO",
bottom_list   = "BBBBBBBBB",
left_list     = "YYYYYYYYY",
right_list    = "WWWWWWWWW"
)

solution_position = {}

for l in range(len(solved_cube.layout)):
    layer = solved_cube.layout[l]

    for r in range(len(layer)):
        row = solved_cube.layout[l][r]

        for b in range(len(row)):

            block_current = solved_cube.layout[l][r][b]
            if(type(block_current) is not Pivot):
                solution_position[  block_current.blk_id  ] = (l, r, b)



def solvable_test(c):
    for layer in c.layout:
        for row in layer:
            for block in row:
                if type(block) is not Pivot:
                    if block.blk_id not in solution_position:
                        return False

    return True
