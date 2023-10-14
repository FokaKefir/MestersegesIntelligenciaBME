import json

from communication_slave import CommunicationSlave
from student_player import StudentPlayer


if __name__ == '__main__':
    init_msg = CommunicationSlave.get_message()
    params = json.loads('\n'.join(init_msg))
    student_player = StudentPlayer(player_index=params['player_index'],
                                   board_size=params['board_size'],
                                   n_to_connect=params['n_to_connect'])
    CommunicationSlave.send_answer('OK')

    while True:
        msg = CommunicationSlave.get_message()
        last_player_col = int(msg[0])
        current_player_col = student_player.step(last_player_col)
        CommunicationSlave.send_answer(str(current_player_col))
