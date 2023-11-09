#!/bin/bash

# The 9 nodes are run with the first being a proposer sending a message instantly
start powershell "java MemberNode 1 true > test_outputs/1_proposer_out.txt"
sleep .5
start powershell "java MemberNode 2 false > test_outputs/1_acceptor_out.txt"
sleep .5
start powershell "java MemberNode 3 false"
sleep .5
start powershell "java MemberNode 4 false"
sleep .5
start powershell "java MemberNode 5 false"
sleep .5
start powershell "java MemberNode 6 false"
sleep .5
start powershell "java MemberNode 7 false"
sleep .5
start powershell "java MemberNode 8 false"
sleep .5
start powershell "java MemberNode 9 false"