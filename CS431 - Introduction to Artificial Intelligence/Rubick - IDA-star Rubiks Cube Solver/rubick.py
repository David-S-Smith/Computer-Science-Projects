"""
Rubick is a python program that solves an inputted 3x3x3 rubick's cube.
In addition to the .py file itself, the program takes an argument representing
the name of a .txt file formatted as a cube layout.

Cube layout files should by 6 lines of 9 characters each, representing reading
each face of the cube from left to right, top to bottom, in the following order:

Start with the red pivot facing you, with the green on top. Assuming a standard cube,
this orients things correctly for the program to run.

Read the red face (for our purposes, any colour face refers to its pivot), then
turn the whole cube 90 degrees forward. Then read the green face, orange face,
and blue face by performing the same motion. Then, turn the cube so that the red
face is again at the front. Turn the the whole cube counter clockwise 90 degrees
 with respect to the top, so that you're looking at the left side (the yellow face).
Then turn the cube 180 degrees with respect to the top to look at the right side
(white face).

This is the order for reading in faces.
"""

import sys
from rubick_cube_blocks import *
from rubick_ida import *

if len(sys.argv) < 2:
    print("Please enter a cube file to be solved. Terminating program.")
    sys.exit(1)

layout_file = open(sys.argv[1])
faces = []

print("Hello! My name is Rubick, and I try and solve rubik's cubes.")

for line in layout_file:
    faces.append(line)

front = faces[0]
top = faces[1]
opposite = faces[2]
bottom = faces[3]
left = faces[4]
right = faces[5]

start = Cube(front_list=front, top_list=top, opposite_list=opposite, bottom_list=bottom, left_list=left, right_list=right)

if(solvable_test(start) == False):
    print("Invalid cube, terminating program.")
    sys.exit(1)

print("Searching for a solution to your cube...")

result = ida_star(start)

if result is None:
    print("Uh oh, that isn't right. I can't solve your cube!")
    sys.exit(1)

path = result[0]
print("Alright, I've got it! From your starting position: \n")

for cube in path:
    move = cube.previous_move
    print(move)

print("\nAnd now your cube should be solved!")
