
// 0 - nothing. 1 - gold. 2 - wumpus. 4 - pit
const gameBoard = [
//   1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20    (cols)
    [0,4,0,0,0,4,4,4,4,0,0,0,0,0,4,4,0,0,0,0],       // row: 20
    [0,4,0,0,0,0,0,0,0,0,0,4,0,4,0,0,0,4,0,0],       // row: 19
    [0,0,0,0,0,4,0,0,0,0,0,0,0,0,0,4,0,4,0,0],       // row: 18
    [0,0,0,0,4,0,4,4,0,0,0,0,0,0,4,0,0,0,0,0],       // row: 17
    [0,0,0,0,0,0,0,0,0,4,0,0,0,0,0,0,4,0,0,4],       // row: 16
    [4,0,0,4,0,0,0,0,0,0,0,4,0,0,0,0,4,0,0,0],       // row: 15
    [4,0,0,0,4,4,0,0,0,4,0,0,0,4,4,0,0,4,0,4],       // row: 14
    [0,0,0,0,0,4,0,0,0,0,0,0,4,0,0,0,4,0,0,0],       // row: 13
    [0,4,0,0,0,0,0,0,0,0,0,0,4,0,4,0,0,0,0,0],       // row: 12
    [0,0,0,0,0,0,0,0,0,0,4,0,0,0,4,0,4,0,0,0],       // row: 11
    [4,4,0,0,4,4,0,0,1,0,0,0,0,0,0,0,4,0,0,0],       // row: 10
    [0,0,0,0,0,0,0,0,4,4,0,4,0,0,0,0,0,4,4,0],       // row: 9
    [4,0,0,0,4,0,0,0,0,4,0,4,0,0,0,0,4,4,0,0],       // row: 8
    [0,0,0,4,0,0,0,0,0,0,4,0,0,0,0,4,0,4,0,0],       // row: 7
    [0,0,0,0,0,4,0,0,0,0,0,0,0,0,0,0,0,4,0,0],       // row: 6
    [0,0,0,0,0,4,0,0,0,0,0,4,0,4,0,0,0,0,0,0],       // row: 5
    [0,0,0,4,0,2,4,4,4,0,0,0,4,0,0,0,0,0,0,4],       // row: 4
    [0,0,0,0,0,0,0,4,0,0,0,4,0,0,0,0,0,0,0,0],       // row: 3
    [4,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,4,0,0],       // row: 2
    [0,0,0,4,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0],       // row: 1
]