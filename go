#!/bin/bash

# Get the current day or use the provided day argument
day=$(printf "%02d" "${1:-$(date +%d)}")

# Copy the template file to the new file and replace "__DAY__" with the current day
cp src/Template.kt src/"Day${day}.kt"
sed -i  "s/__DAY__/$day/" src/"Day${day}.kt"

# Prompt for test answer and replace "-42" with the provided answer
read -p "Enter the test answer: " test_answer
sed -i "s/-42/$test_answer/" src/"Day${day}.kt"

# Wait for keypress and write the paste buffer to Day${day}_test.txt
read -p "Press Enter after copying test input..." -n 1 -r
echo ""  # Move to a new line after the keypress
pbpaste > src/"Day${day}_test.txt"

# Wait for another keypress and write the paste buffer to Day${day}.txt
read -p "Press Enter after copying input..." -n 1 -r
echo ""  # Move to a new line after the keypress
pbpaste > src/"Day${day}.txt"

echo "Setup completed for Day $day"
