import sys
from typing import List


class CommunicationSlave:
    @staticmethod
    def get_message() -> List[str]:
        # parse params
        first_line = sys.stdin.readline()
        n_msg_lines = int(first_line)

        # read full answer message
        lines = []
        for _ in range(n_msg_lines):
            lines.append(sys.stdin.readline())

        return lines

    @staticmethod
    def send_answer(answer: str = None) -> None:
        n_answer_lines = 0
        if answer is not None:
            n_answer_lines = len(answer.splitlines())

        # send answer
        sys.stdout.write(f"{n_answer_lines}\n{answer}\n")
        sys.stdout.flush()
