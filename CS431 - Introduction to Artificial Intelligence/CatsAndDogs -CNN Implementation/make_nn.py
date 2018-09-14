import numpy as np

from keras import models
from keras.layers import Conv2D, MaxPooling2D
from keras.layers import Activation, Dropout, Flatten, Dense
from keras import backend
import keras
import os, cv2, sys

"""
This program creates and saves .dnn models to classify cat and dog images. Besides the program filename, the program
takes 2 arguments: A name for the model to be saved as, (the filetype ".dnn" is appended to this name, and not needed
in the argument) and the directory for the data_set that will be used for training. Files for training are assumed to
be images with a "c" or "d" at the start of their name to indicate if they are a cat or dog respectively.
"""

__author__ = "David S Smith"
__date__ = "April 30, 2018"




def _prepare_data(path):
	"""
	Takes in a directory path in the form of a string representing the path to the inside of the folder containing the images.
	Outputs, in order: the training images, and their respective classifications.
	"""

	training_data_list = []
	training_labels_list = []

	for filename in os.listdir(path):
		letter = filename[0]
		label = -1

		f = str(path) + str(filename)

		if letter == "c":
			label = (1,0)
		elif letter == "d":
			label = (0,1)
		else:
			print("Error: UNABLE TO CLASSIFY : " + str(filename))
			sys.exit(1)

		img = cv2.imread(f)

		training_data_list.append(img)
		training_labels_list.append(label)

	training_data = np.array(training_data_list)
	training_labels = np.array(training_labels_list)

	return training_data, training_labels


if len(sys.argv) < 3:
	print("Error, invalid number of arguments. Closing program.")
	sys.exit(1)

dir = sys.argv[1]
name_for_file = sys.argv[2]

print("\n\n Preparing image data_set for use in model")

training_data, training_labels = _prepare_data(dir)

model = models.Sequential()
input_shape = (100,100,3)
#
model.add(Conv2D(32, (5, 5), input_shape=input_shape, activation = 'relu'))
model.add(MaxPooling2D(pool_size=(2,2)))

model.add(Conv2D(16, (3, 3), activation = 'relu'))

model.add(Conv2D(16, (3, 3), activation = 'relu'))
model.add(MaxPooling2D(pool_size=(2,2)))
#
model.add(Conv2D(64, (3, 3), activation = 'relu'))
model.add(MaxPooling2D(pool_size=(2,2)))

model.add(Conv2D(32, (3, 3), activation = 'relu'))
model.add(MaxPooling2D(pool_size=(2,2)))

model.add(Flatten())

model.add(Dense(32, activation = 'relu'))
model.add(Dropout(0.5))

model.add(Dense(2, activation = 'softmax'))

# model.summary() Uncomment to see structure

print("Compiling model...")

model.compile(
			  loss = 'binary_crossentropy',
			  optimizer = 'adam',
			  metrics = ['accuracy']
			  )

save_as = str(name_for_file) + ".dnn"

model.fit(training_data, training_labels, batch_size = 200, epochs = 50, verbose = 1)

print("\n\nSaving file...")
model.save(save_as, include_optimizer = False)
print("File saved as " + str(save_as) + ", closing program")
sys.exit(0)
