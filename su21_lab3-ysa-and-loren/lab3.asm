#ysabella atehortua and loren

.data

messg1: .asciiz "Please enter the first number: "
messg2: .asciiz "Please enter the second number:  "
size: .asciiz "Length of sequence: "
newLine:  .asciiz "\n"
endline:.asciiz "\n"
space:.asciiz  " "

.text
main:

la $a0, messg1 #Set up my message as an argument - la is Load Address
addi $v0, $zero, 4 #Specify system call 4 to print
syscall #make the system call

addi $v0, $zero, 5 #set up syscall to read number in from command line
syscall
add $t0, $v0, $zero #move number that was just entered into register $t0

#print message 2

la $a0, messg2 #Set up my message as an argument - la is Load Address
addi $v0, $zero, 4 #Specify system call 4 to print
syscall #make the system call

addi $v0, $zero, 5 #set up syscall to read number in from command line
syscall
add $t1, $v0, $zero #move number that was just entered into register $t1

#initialize value of next number in sequence to 0
li   $t3, 0

#initial value needed for loop
li   $t4, 0
li   $t5, 0

#set value for length of sequence
la $a0, size
addi $v0, $zero, 4
syscall

li   $v0, 5
syscall
move $t6, $v0

#Print first value in sequence
la   $a0, ($t0)
li   $v0, 1
syscall
la   $a0, space
li   $v0, 4
syscall
la   $a0, ($t0)
li   $v0, 34
li   $t7, 0
syscall
la   $a0, space
li   $v0, 4
syscall

first:
andi $t8, $t0, 1
add  $t7, $t7, $t8
srl  $t3, $t3, 1
bne  $t3, $zero, first

move $a0,$t7
li   $v0,1
syscall

la   $a0, newLine
li   $v0, 4
syscall

#Print second value in sequence
la   $a0, ($t1)
li   $v0, 1
syscall
la   $a0, space
li   $v0, 4
syscall
la   $a0, ($t1)
li   $v0, 34
syscall
la   $a0, space
li   $v0, 4
syscall

second:
andi $t8, $t1, 1
add  $t7, $t7, $t8
srl  $t3, $t3, 1
bne  $t3, $zero, second

move $a0,$t7
li   $v0,1
syscall

la   $a0, newLine
li   $v0, 4
syscall

#loop for next number
add.d $f0, $f2, $f4
addi $t9, $t6, -2
loop:
add  $t3, $t0, $t1
move $t0, $t1
move $t1, $t3
addi $t9, $t9, -1
la   $a0, ($t3)
jal  print
bgtz $t9, loop

li   $v0, 10
syscall

#Print commands
print:
li   $v0, 1
syscall

la   $a0, space
li   $v0, 4
syscall

la   $a0, ($t3)
li   $v0, 34
syscall

la   $a0, space
li   $v0, 4
syscall

li   $t7, 0

one:
andi $t8, $t3, 1
add  $t7, $t7, $t8
srl  $t3, $t3, 1
bne  $t3, $zero, one

move $a0,$t7
li   $v0,1
syscall

la   $a0, newLine
li   $v0, 4
syscall
jr   $ra