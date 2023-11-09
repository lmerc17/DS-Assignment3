#!/bin/bash

# The 9 nodes are run with the first and second being proposers sending their messages instantly
start powershell "java MemberNode 1 true > test_outputs/2_proposer1_out.txt"
sleep .5
start powershell "java MemberNode 2 true > test_outputs/2_proposer2_out.txt"
sleep .5
start powershell "java MemberNode 3 false > test_outputs/2_acceptor_out.txt"
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