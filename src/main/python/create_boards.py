import random

LENGTH = 10
FIRST_BOARD = 1
LAST_BOARD = 5
PIT_RATIO = 0.2
MAX_PITS = LENGTH * LENGTH * PIT_RATIO

def is_first_cell(row, col):
    return row == LENGTH - 1 and col == 0


def get_cell_not_first():
    row = random.randint(0, LENGTH - 1)
    col = random.randint(0, LENGTH - 1)
    if not is_first_cell(row, col):
        return row, col
    else:
        return get_cell_not_first()


def create_board():
    board = []
    for i in range(LENGTH):
        board.append([0] * LENGTH)

    num_of_pits = 0
    for row, _ in enumerate(board):
        for col, _ in enumerate(board[row]):
            if random.random() < PIT_RATIO and not is_first_cell(row, col) and num_of_pits <= MAX_PITS:
                board[row][col] += 4
                num_of_pits += 1

    row, col = get_cell_not_first()
    board[row][col] += 1

    row, col = get_cell_not_first()
    board[row][col] += 2

    result = """
// 0 - nothing. 1 - gold. 2 - wumpus. 4 - pit
const gameBoard = [
//   """ + ','.join([str(a) for a in range(1, LENGTH + 1)]) + "    (cols)"

    for ind, row in enumerate(board):
        result += "\n    ["
        result += ','.join([str(cell) for cell in row])
        # for cell in row:
        #     result += str(cell)
        result += "],       // row: " + str(LENGTH - ind)
    result += "\n]"

    return result


for i in range(FIRST_BOARD, LAST_BOARD+1):
    with open(f"board{i}.js", "w") as f:
        f.write(create_board())
