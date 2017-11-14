/**
  The PrioQueue is a FIFO data structure, but it prioritizes values based off of a priority score. Items with a greater score will be removed sooner.

  @author   David S Smith
  @version  9/29/2017
*/

class PrioQueue<E>{

    private E[] elems;
    private int[] scores;
    private int[] times;
    private int size;
    private int timeStamp;

    private final int BASE_SIZE = 20;

/**
  Creates a empty Priority Queue.
*/
    public PrioQueue(){
        size = 0;
        timeStamp = 0;

        elems = (E[]) new Object[BASE_SIZE];
        times = new int[BASE_SIZE];
        scores = new int[BASE_SIZE];
    }
/**
  Adds an item with a given priority score into the queue and settles it into the queue.
  @param item The item to be added
  @param score The priority score of the item. Larger numbers mean higher priority, and will exit the queue sooner.
*/
    public void insert(E item, int score){
        //make timestamp for this item being added
        timeStamp++;

        if(size == 0){
            elems[0] = item;
            times[0] = timeStamp;
            scores[0] = score;
            size++;
            return;
        }

        //if the insertion into the array is going to go out of the array bounds, increase arrays' sizes
        if(size+1 > scores.length){
            doubleArray();
        }

        int currentIndex = size;
        scores[currentIndex] = score;
        elems[currentIndex] = item;
        times[currentIndex] = timeStamp;
        currentIndex = size;
        int parentIndex = findParent(currentIndex);

        boolean unfinished = true;

        //until there are no parents above OR the score of the parent exceeds the score of this element
        while(scores[currentIndex] > scores[parentIndex]){ //while our parent is larger than us
            swap(currentIndex, parentIndex); //swap with parent
            currentIndex = parentIndex;
            parentIndex = findParent(currentIndex);
        }
        size++;
        return;
    }


/**
  Removes the highest priority item, based first off of score, then off of how long the item has been in the queue (longer wait time items will be prioritized)
  @return The element of highest priority. Will not include the item's score.
*/
    public E remove(){
        //store removed stuff for later use
        E removedElem = elems[0];
        int removedScore = scores[0];
        int removedTime = times[0];
        //move last element up to top
        elems[0] = elems[size-1];
        scores[0] = scores[size-1];
        times[0] = times[size-1];

        int currentIndex = 0;
        int largerChild = -1;
        int largerChildIndex = -1;

        sinking:
        do{
            int rKidIndex = rightChild(currentIndex);
            int lKidIndex = leftChild(currentIndex);
            if((rKidIndex >= size) || (lKidIndex >= size)){
                break sinking;
            }
            int rKid = scores[rKidIndex];
            int lKid = scores[lKidIndex];

            if((rKid == 0) || (lKid == 0)){//if one/both side/s has no child
                if(lKid == 0){ //no left child, ie, no children
                    size--;
                    return removedElem; //just return child
                }
                else{ //right child does not exist, but left one does
                    swap(currentIndex, lKidIndex);
                    size--;
                    return removedElem;
                }
            }

            //choose larger child and its index
            if(rKid > lKid){
                largerChild = rKid;
                largerChildIndex = rKidIndex;
            }
            else if(rKid < lKid){
                largerChild = lKid;
                largerChildIndex = lKidIndex;
            }
            else{ //neither child is larger, choose off of time stamps
                if(times[rKidIndex] > times[lKidIndex]){
                    largerChild = rKid;
                    largerChildIndex = rKidIndex;
                }
                else{
                    largerChild = lKid;
                    largerChildIndex = lKidIndex;
                }
            }

            //swap and continue
            swap(largerChildIndex, currentIndex);
            currentIndex = largerChildIndex;

        }while(largerChild > scores[currentIndex]); //while the larger of the two children is greater than the last value
        size--;
        return removedElem;
    }


/**
  Returns the size the Queue.
  @return The number of items in the queue.
*/
    public int getSize(){
        return size;
    }



/**
  Clears queue, resetting size to 0 and removing all items.
*/
    public void clear(){
        scores = new int[BASE_SIZE];
        elems = (E[]) new Object[BASE_SIZE];
        times = new int[BASE_SIZE];
        size = 0;
        return;
    }

    //copies contents of all arrays into a set of larger arrays
    private void doubleArray(){
        int newSize = scores.length*2;

        //create new arrays in temporary variables
        int[] newScores = new int[newSize];
        int[] newTimes = new int[newSize];
        E[] newElems = (E[]) new Object[newSize];

        for(int i = 0; i < size; i++){

            //copy over all values
            newScores[i] = scores[i];
            newElems[i] = elems[i];
            newTimes[i] = times[i];

        }
        //change pointers to direct elems and scores to their larger arrays
        elems = newElems;
        scores = newScores;
        times = newTimes;

        return;
    }

    //swaps contents of of score, time, and element arrays based on two indices
    private void swap(int a, int b){
        int tempTime = times[a];
        int tempScore = scores[a];
        E tempItem = elems[a];

        times[a] = times[b];
        scores[a] = scores[b];
        elems[a] = elems[b];

        times[b] = tempTime;
        scores[b] = tempScore;
        elems[b] = tempItem;
    }

    //finds parent index of a given index
    private int findParent(int index){
        return (int)((index-1)/2);
    }
    //finds left child of a given index
    private int leftChild(int index){
        return 2*index + 1;
    }
    //finds right child of a given index
    private int rightChild(int index){
        return 2*index + 2;
    }

}
