"""
This program classifies a series of image files using a convolutional neural net.
The first argument (besides the program itself) is the .dnn file model to be used.
All proceeding arguments are filenames for images.
"""


__author__ = "David S Smith"
__date__ = "April 28, 2018"




from keras import models
import sys, cv2
import numpy as np

if len(sys.argv) < 3:
    print("Missing argument: please specify a .dnn file and a list of images (In that order)")
    sys.exit(1)

cnn = sys.argv[1]

if (cnn[-4:] != ".dnn"):
    print("First argument filetype wrong: First argument must be a .dnn file.")
    sys.exit(1)

i = 0

model = models.load_model(cnn)
to_classify = []

for i in range(2, len(sys.argv)):
    image_file_name = sys.argv[i]
    img_np = cv2.imread(image_file_name)
    to_classify.append(img_np)

to_classify_np = np.array(to_classify)

print("\n\nClassifying from image files...\n")
predictions = model.predict_on_batch(to_classify_np)

CAT = 0
DOG = 0

i = 2
for pred in predictions:
    amount_cat = pred[0]
    amount_dog = pred[1]
    file_name = sys.argv[i]

    if amount_cat > amount_dog:
        CAT+=1
        print(str(file_name) + " ~ Cat")
    elif amount_cat < amount_dog:
        DOG+=1
        print(str(file_name) + " ~ Dog")
    else:
        print(str(file_name) + " ~ Cat (Probably)") #THIS ONLY RUNS IF THE PREDICTION IS COMPLETELY EVEN.

    i+=1

print("\nFinal results: " + str(CAT) + " cats and " + str(DOG) + " dogs")
print("\nAll images processed. Have a nice day.")

sys.exit(0)
