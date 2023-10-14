from typing import List

from board import Board

class StudentPlayer:
    def __init__(self, player_index: int, board_size: List[int], n_to_connect: int):
        self.__n_to_connect = n_to_connect
        self.__board_size = board_size
        self.__player_index = player_index

        self.__board = Board(self.__board_size, self.__n_to_connect)


    def step(self, last_player_col: int) -> int:
        """
        One step (column selection) of the current player.
        :param last_player_col: [-1, board_size[1]), it is -1 if there was no step yet
        :return: step (column index) of the current player
        """
        if last_player_col != -1:
            self.__board.step(self._other_player_index, last_player_col)

        col = 0  # your logic here
        self.__board.step(self.__player_index, col)

        return col
