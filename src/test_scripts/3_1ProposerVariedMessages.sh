#!/bin/bash

# The 9 nodes are run with the first being a proposer and the rest having varying message response times
start powershell "java MemberNode 1 true 0 > test_outputs/3_proposer1_out.txt"
sleep .5
start powershell "java MemberNode 2 false 0 > test_outputs/3_acceptor_out.txt"
sleep .5
start powershell "java MemberNode 3 false 0"
sleep .5
start powershell "java MemberNode 4 false 0"
sleep .5
start powershell "java MemberNode 5 false 0"
sleep .5
start powershell "java MemberNode 6 false 3 > test_outputs/3_acceptor_short_delay_out.txt"
sleep .5
start powershell "java MemberNode 7 false 3"
sleep .5
start powershell "java MemberNode 8 false 15"
sleep .5
start powershell "java MemberNode 9 false 15 > test_outputs/3_acceptor_long_delay_out.txt"