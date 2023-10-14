from typing import Tuple, List

import numpy as np


class Board:
    def __init__(self, board_size: Tuple[int, int], n_to_connect: int):
        self.__state = np.zeros(board_size, dtype=int)
        self.__board_size = board_size
        self.__n_to_connect = n_to_connect

        self.__n_steps = 0
        self.__last_player_index = None
        self.__last_player_row = None
        self.__last_player_column = None
        self.__winner = None

    def copy(self):
        new_board = Board(self.__board_size, self.__n_to_connect)
        new_board.__state = self.__state.copy()
        new_board.__board_size = self.__board_size
        new_board.__n_to_connect = self.__n_to_connect
        new_board.__n_steps = self.__n_steps
        new_board.__last_player_index = self.__last_player_index
        new_board.__last_player_row = self.__last_player_row
        new_board.__last_player_column = self.__last_player_column
        new_board.__winner = self.__winner

        return new_board

    def get_state(self) -> np.ndarray:
        return self.__state

    def step_is_valid(self, column: int):
        valid_col_index = 0 <= column < self.__board_size[1]
        if not valid_col_index:
            return False
        is_space_on_the_top = self.__state[0][column] == 0
        return is_space_on_the_top

    def get_valid_steps(self) -> List[int]:
        valid_cols = []
        col = 0
        while col < self.__board_size[1]:
            if self.step_is_valid(col):
                valid_cols.append(col)
            col += 1
        return valid_cols

    def step(self, player_index: int, column: int) -> None:
        self.__n_steps += 1
        self.__last_player_index = player_index
        self.__last_player_column = column
        row = self.__board_size[0] - 1
        while row >= 0:
            if self.__state[row][column] == 0:
                self.__state[row][column] = player_index
                self.__last_player_row = row
                return
            row -= 1

    def game_ended(self) -> bool:
        # board is unused
        if self.__last_player_index is None:
            return False
        # player won
        last_player_won = (
            self.is_n_in_a_row(self.__last_player_row, self.__last_player_column, self.__last_player_index) or
            self.is_n_in_a_col(self.__last_player_row, self.__last_player_column, self.__last_player_index) or
            self.is_n_diagonally(self.__last_player_row, self.__last_player_column, self.__last_player_index) or
            self.is_n_skew_diagonally(self.__last_player_row, self.__last_player_column, self.__last_player_index))
        if last_player_won:
            self.__winner = self.__last_player_index
            return True
        # table is full
        table_is_full = self.__n_steps == self.__board_size[0] * self.__board_size[1]
        if table_is_full:
            self.__winner = 0
            return True
        return False

    def is_n_in_a_row(self, row: int, col: int, player_index: int) -> bool:
        n_in_a_row = 0
        start_col = max(0, col - self.__n_to_connect + 1)
        end_col = min(self.__board_size[1], col + self.__n_to_connect)
        c = start_col
        while c < end_col:
            if self.__state[row][c] == player_index:
                n_in_a_row += 1
                if n_in_a_row == self.__n_to_connect:
                    self.__winner = player_index
                    return True
            else:
                n_in_a_row = 0
            c += 1
        return False

    def is_n_in_a_col(self, row: int, col: int, player_index: int) -> bool:
        n_in_a_col = 0
        start_row = max(0, row - self.__n_to_connect + 1)
        end_row = min(self.__board_size[0], row + self.__n_to_connect)
        r = start_row
        while r < end_row:
            if self.__state[r][col] == player_index:
                n_in_a_col += 1
                if n_in_a_col == self.__n_to_connect:
                    self.__winner = player_index
                    return True
            else:
                n_in_a_col = 0
            r += 1
        return False

    def is_n_diagonally(self, row: int, col: int, player_index: int) -> bool:
        n_in_a_diagonal = 0
        step_left_up = min(self.__n_to_connect - 1, min(row, col))
        step_right_down = min(self.__n_to_connect, min(self.__board_size[0] - row, self.__board_size[1] - col))
        if (step_left_up + step_right_down) < self.__n_to_connect:
            return False
        diagonal_step = -step_left_up
        while diagonal_step < step_right_down:
            if self.__state[row + diagonal_step][col + diagonal_step] == player_index:
                n_in_a_diagonal += 1
                if n_in_a_diagonal == self.__n_to_connect:
                    self.__winner = player_index
                    return True
            else:
                n_in_a_diagonal = 0
            diagonal_step += 1
        return False

    def is_n_skew_diagonally(self, row: int, col: int, player_index: int) -> bool:
        n_in_a_skew_diagonal = 0
        step_left_down = min(self.__n_to_connect - 1, min(self.__board_size[0] - row - 1, col))
        step_right_up = min(self.__n_to_connect, min(row + 1, self.__board_size[1] - col))
        if (step_right_up + step_left_down) < self.__n_to_connect:
            return False
        skew_diagonal_step = -step_left_down
        while skew_diagonal_step < step_right_up:
            if self.__state[row - skew_diagonal_step][col + skew_diagonal_step] == player_index:
                n_in_a_skew_diagonal += 1
                if n_in_a_skew_diagonal == self.__n_to_connect:
                    self.__winner = player_index
                    return True
            else:
                n_in_a_skew_diagonal = 0
            skew_diagonal_step += 1
        return False

    def get_winner(self) -> int:
        return self.__winner

    def get_last_player_index(self) -> int:
        return self.__last_player_index

    def get_last_player_row(self) -> int:
        return self.__last_player_row

    def get_last_player_column(self) -> int:
        return self.__last_player_column
