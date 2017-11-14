% bertgame evaluates true a given integer size if the second argument is a list of commands
% (clicking or not clicking a tile, read left to right) that can be used to begin a solution
% (that is, after completing those clicks on the first row, no more clicks need be performed
% on that row). Thus, querying bertgame with some number and a variable in the second argument
% can be used to generate possible solutions to a Bert Bos puzzle of size Size.
%
% Description of the Bert Bos Puzzle: (taken from Tony Mullen's assignment page: http://mathcs.pugetsound.edu/~tmullen/pages/bertspel/)
% "The puzzle consists of a square of n x n blue tiles. The back side of each tile is red.
% The object of the puzzle is to turn every tile, so that the whole square becomes red.
% However, when a tile is turned, its four neighbors also turn. With every turn, therefore,
% five tiles swap color.""

% Sara Wille and David Smith, 5/1/2017

bertgame(Size, FirstRow):-
    %generate board
    isMatrix(Size, Board),
    rightSize(Size, FirstRow),
    !,
    changeRows(FirstRow, Board).

flip(red, blue).
flip(blue, red).

isMatrix(Size, [H|T]):-
    rightSize(Size, [H|T]),
    numRows(Size, [H|T]).

numRows(_, []).
numRows(Size, [H|T]):-
    rowSize(Size, H),
    numRows(Size, T).

rightSize(0, []).
rightSize(Size, [_|T]):-
    NewSize is (Size-1),
    rightSize(NewSize, T).

rowSize(0, []).
rowSize(Size, [blue|T]):-
    NewSize is (Size-1),
    rowSize(NewSize, T).

checkRowRed([]). %if all squares are red, success
checkRowRed([red|T]):- %recurse along the row so long as each square is red
  checkRowRed(T).

%If you've reached the end of the commands
changeRows(Commands, [LastRow|[]]):-
  startApplyCurrent(Commands, LastRow, ClickedLastRow),
  checkRowRed(ClickedLastRow).

changeRows(Commands, [CurrentRow, NextRow|RemainingRows]):-
  startApplyCurrent(Commands, CurrentRow, ClickedCurrentRow), %Apply commands using a rule specific to if we're at the beginning of a row
  applyNext(Commands, NextRow, ClickedNextRow), %Apply changes to the row below us, based on what's happening on the current row
  createCommands(ClickedCurrentRow, NewCommands), %Look at the current row after applying commands, and figure out what will need to be clicked in the next row
  changeRows(NewCommands, [ClickedNextRow|RemainingRows]). %Start again on the next row, until the base case listed above

createCommands([],[]). %The command we would create for nothing is nothing

createCommands([blue|Squares], [click|SubCommands]):- %If the current row has a blue tile, we know the next row will have a click on that same slot
  createCommands(Squares, SubCommands).

createCommands([red|Squares], [noClick|SubCommands]):- %If the current row has a red tile, we know no click will occur on that slot
  createCommands(Squares, SubCommands).

%To be clear, applyNext is being called in addition to a applyCurrent. Basically this is how we change rows below us easily. This row is the new current row after we're done with the row we're looking at
applyNext([], [], []). %We reach the end of the row

applyNext([click|Commands], [Colour|T], [NewColour|SubNew]):- %If the command calls for a click at this slot, we change it's color
  flip(Colour, NewColour),
  applyNext(Commands, T, SubNew). %recurse to the next slot and command

applyNext([noClick|Commands], [N|T], [N|SubNew]):- %if the command calls for no click, we just keep looking
  applyNext(Commands, T, SubNew).

%The first tile if we don't click
startApplyCurrent([click], [blue], [red]).
startApplyCurrent([noClick|Commands], Squares, NewRow):-
  applyCurrent(Commands, Squares, NewRow).

%The first tile is clicked, things happen
startApplyCurrent([click|Commands], [Sq1, Sq2|Squares], NewRow):-
  %Swapping colours of first two tiles
  flip(Sq1, NewSq1),
  flip(Sq2, NewSq2),
  %Recurse down row, applying changes
  applyCurrent(Commands, [NewSq1, NewSq2|Squares], NewRow). %We view heads 2 at a time, but step 1 at a time

%the last tile is a noClick(Base Case)
applyCurrent([noClick], [Sq1, Sq2], [Sq1, Sq2]).

%The last tile is a click (Base case)
applyCurrent([click], [Sq1,Sq2], NewRow):-
  %Swap colours
  flip(Sq1, NewSq1),
  flip(Sq2, NewSq2),
  NewRow = [NewSq1, NewSq2].

%the current and next term aren't to be clicked, so we can move ahead one
applyCurrent([noClick|Commands], [Sq|Squares], NewRow):-
  applyCurrent(Commands,Squares,NewSubRow), %recurse, lopping off one command and tile
  NewRow = [Sq|NewSubRow].

%The next command will be a click. We will thus change the tile before it, after it, and the clicked tile
applyCurrent([click|Commands], [Sq1,Sq2,Sq3|Squares], [NewSq1|NewSubRow]):-
  %Swap Colours
  flip(Sq1, NewSq1),
  flip(Sq2, NewSq2),
  flip(Sq3, NewSq3),
  applyCurrent(Commands, [NewSq2,NewSq3|Squares], NewSubRow). %Recurse, lop off one
