#!/bin/bash

# The 9 nodes are run with the first being a proposer sending a message instantly
start powershell "java MemberNode 1 true 0 > test_outputs/1_proposer_out.txt"
sleep .5
start powershell "java MemberNode 2 false 0 > test_outputs/1_acceptor_out.txt"
sleep .5
start powershell "java MemberNode 3 false 0"
sleep .5
start powershell "java MemberNode 4 false 0"
sleep .5
start powershell "java MemberNode 5 false 0"
sleep .5
start powershell "java MemberNode 6 false 0"
sleep .5
start powershell "java MemberNode 7 false 0"
sleep .5
start powershell "java MemberNode 8 false 0"
sleep .5
start powershell "java MemberNode 9 false 0"