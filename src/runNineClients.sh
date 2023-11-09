#!/bin/bash

# running 9 nodes at one time
# there is a delay between them so they connect to the CCC in the right order
start powershell java MemberNode 1 true
sleep .5
start powershell java MemberNode 2 true
sleep .5
start powershell java MemberNode 3 false
sleep .5
start powershell java MemberNode 4 false
sleep .5
start powershell java MemberNode 5 false
sleep .5
start powershell java MemberNode 6 false
sleep .5
start powershell java MemberNode 7 false
sleep .5
start powershell java MemberNode 8 false
sleep .5
start powershell java MemberNode 9 false