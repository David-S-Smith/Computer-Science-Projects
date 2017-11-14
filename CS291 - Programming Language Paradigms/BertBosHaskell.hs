-- BERT BOS: Haskell Implementation

-- Brief Explanation of the Bert Bos Puzzle: (taken from Tony Mullen's assignment page: http://mathcs.pugetsound.edu/~tmullen/pages/bertspel/)
-- "The puzzle consists of a square of n x n blue tiles. The back side of each tile is red. The object of the puzzle is to turn every tile, so that the whole square becomes red.
-- However, when a tile is turned, its four neighbors also turn. With every turn, therefore, five tiles swap color.""

-- Use of bertgame function will produce a list of lists containing "commands" (to click or not click the tile, reading left to right)
-- Each list is a valid starting list of clicks to fully change a bert-bos puzzle grid from blue to red (each click flips the tile clicked and any tile cardinally adjacent to it)
-- bertgame takes an integer argument that represents the length of one side of the puzzle (and assumes the puzzle is a square)

-- David S Smith, 5/9/2017



data Colour  = Red|Blue deriving (Show, Eq)
data Command = Click|NoClick deriving (Show, Eq)

--Red to Blue, Blue to Red
switch tile | tile == Red  = Blue
            | tile == Blue = Red

--n x n board of Blue tiles
generateBoard :: Int -> [[Colour]]
generateBoard n = replicate n (replicate n Blue)


--the final list of results should be taken from possible commands and then tested to verify they produce a fully red board
bertgame :: Int -> [[Command]]
bertgame n = [results | results <- (possibilities), (testing (results) (board))]
                      where board = generateBoard n
                            possibilities = generatePossibilities n


--All possible click/noclick command lists of length n in a list of lists
generatePossibilities :: Int -> [[Command]]
generatePossibilities n = sequence (replicate n [Click, NoClick])

testing :: [Command] -> [[Colour]] -> Bool
testing commands [lastRow] = isRed (startApplyCurrent commands lastRow)
testing commands (b:board) =
                          --start clicking
                          --IN: COMMANDS, ROW OF BOARD
                          --OUT: ROW OF BOARD, ALTERED
                          -- isRed (clickedRow)
                          -- &&
                          (testing (nextCommands) (newBoard))
                          --SOMETHING nextRow

                          where clickedRow   = startApplyCurrent commands b
                                nextRow      = (applyNext commands (head board)) --the next row, altered and ready for clicks
                                nextCommands = generateCommands clickedRow
                                newBoard     = nextRow:(drop 1 board)

--Keep going down the row until you find a not red tile
isRed :: [Colour] -> Bool
isRed [Red] = True
isRed (first:remaining) | first == Red = isRed remaining
                        | otherwise    = False

--click the next row wherever this row has a blue tile
generateCommands :: [Colour] -> [Command]
generateCommands [] = []
generateCommands (first:remaining) | first == Red = (NoClick:(generateCommands remaining))
                                   | otherwise    = (Click  :(generateCommands remaining))



applyNext :: [Command] -> [Colour] -> [Colour]
applyNext [] [] = []
applyNext (c:commands) (first:remaining) | c == Click = (switch first):(applyNext commands (remaining))
                                         | otherwise  =        (first):(applyNext commands (remaining))




startApplyCurrent :: [Command] -> [Colour] -> [Colour]
--If the current command at the first slot in the first row is a click, flip the first and second tile and proceed
--Otherwise proceed

startApplyCurrent [Click] [Blue] = [Red]

startApplyCurrent (c:commands) (first:second:remaining)   | c == Click = afterClick
                                                          | otherwise  = (applyCurrent commands (first:(second:remaining)))
                                                          where first'     = switch first
                                                                afterClick = applyCurrent (commands) (first':((switch second):(remaining)))

applyCurrent (c) [tile1, tile2] | c == [Click] = [(switch tile1), (switch tile2)]
                                | otherwise    = [tile1, tile2]

applyCurrent (c:commands) (first:second:third:remaining)  | c == Click = first':afterClick
                                                          | otherwise  = first:(applyCurrent commands (second:third:remaining))
                                                          where first'     = switch first
                                                                second'    = switch second
                                                                third'     = switch third
                                                                afterClick = applyCurrent (commands) (second':third':remaining)
