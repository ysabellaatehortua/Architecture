#ysabella atehortua and loren behumi-davis

.data

newLine:  .asciiz "\n"
space:	.asciiz  " "

.text

#make space for the array
#referenced https://stackoverflow.com/questions/19436316/array-size-known-only-at-runtime-in-mips

#allocating memory on the heap
li	  $v0, 5	#get array size from user
syscall
add	  $s1, $v0, $zero
mult	  $v0, $t9  #multiply by 2, for both elements of the pair
li	  $t8, 4
mult	  $v0, $t8	#multiply by 4 for the space
mflo	  $a0

#using syscall 9, sbrk to allocate mem to heap
li	  $v0, 9	#address of allocated memory returned in $v0
syscall
add	  $s0, $v0, $zero
add	  $t9, $v0, $zero
li	  $t1, 0

getinput:
	beq	  $t1, $s1, end #loop for all elements, ends when num pairs = n
	addi	  $t1, $t1, 1

	li	  $v0, 5         
	syscall
	sw	  $v0, ($t9)

	li	  $v0, 5
	syscall

	sw	  $v0, 4($t9)	#increment to next word
	addi	  $t9, $t9, 4
	addi	  $t9, $t9, 4
	j getinput

getdist:
	mult	  $a0, $a0 #square the first integer
	mflo	  $a0
	mult	  $a1, $a1 #square second integer
	mflo	  $a1
	add	  $v0, $a0, $a1	#add per dist formula
	jr	  $ra

end:
	sll 	  $t9, $s1, 2	#shift
	add	  $s3, $s0, $zero
	add	  $s2, $t9, $s0	#move array into s2

#bubble sort in mips

bubblesort:
	addi	  $t9, $s3, 8
	beq	  $t9, $s2 skip

	lw	  $a0, 0($s3) 	#load the first coordinate
	lw	  $a1, 4($s3)	#load the second coordinate

	#find distance between points
	jal	  getdist
	add	  $t9, $zero, $v0	
	lw	  $a0 8($s3)
	lw	  $a1 12($s3)

	#compare distances
	jal	  getdist
	add	  $t2, $zero, $v0
	slt	  $t3, $t9, $t2		#compare the two distances
	beq	  $t3, 1, callsort	#if t3 = 1, continue down list
	bgt	  $t9, $t2, swap	#else if t9 > t2, swap

callsort:
	addi	  $s3, $s3, 8
	j	  bubblesort

swap:
	#get first pair
	lw	  $t1, 0($s3)
	lw	  $t9, 4($s3)

	#get second pair
	lw	  $t2, 8($s3)
	lw	  $t3, 12($s3)

	#swap the pairs
	sw	  $t1, 8($s3)
	sw	  $t2, 0($s3)
	sw	  $t9, 12($s3)
	sw	  $t3, 4($s3)
	j callsort		#continue sort

skip:
	addi	  $s2, $s2, -8
	add	  $s3, $s0, $zero
	add	  $t9, $s3, 8
	beq	  $s2, $t9, final

j bubblesort

exit:
	li $v0, 10
	syscall

final:
	li	  $t1, 0
	move	  $t9, $s0

#print
la $a0, newLine
li $v0, 4
syscall

print:
	beq	  $t1, $s1, exit	#when done printing, exit loop
	addi	  $t1, $t1, 1

	lw $a0, ($t9)
	li $v0, 1
	syscall

	la $a0, space
	li $v0, 4
	syscall

	lw $a0, 4($t9)
	li $v0, 1
	syscall

	la $a0, newLine
	li $v0, 4
	syscall

addi $t9, $t9, 8
j print
