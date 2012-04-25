package edu.wmich.demo;

public class CompTest1 {
	
	
	public static void main(String args[]) {
		int x ,a;
		
		x = 10;
		a = 23;
		
		while (x < 1){
			if ( a> 0)
				a = a + x;
		}
		
		while (x >= 1)
		{
			if ( a> 0)
				a = a + x;
			else
			{
				x = x -a;
			}
		}
	}

}

/*

Part I:
int x, a;
 parent node is public static void main(String args[]){
  int x, a;
  x=10;
  a=23;
  while (x < 1)   if (a > 0)   a+=x;
  while (x >= 1) {
    if (a > 0)     a+=x;
 else {
      x=x - a;
    }
  }
}
x=10;
 sibling node is int x, a;

a=23;
 sibling node is x=10;

while (x < 1) if (a > 0) a+=x;
 sibling node is a=23;

if (a > 0) a+=x;
 parent node is while (x < 1) if (a > 0) a+=x;
a+=x;
 parent node is if (a > 0) a+=x;
Is in Else Branch: false

while (x >= 1) {
  if (a > 0)   a+=x;
 else {
    x=x - a;
  }
}
 sibling node is while (x < 1) if (a > 0) a+=x;

if (a > 0) a+=x;
 else {
  x=x - a;
}
 parent node is while (x >= 1) {
  if (a > 0)   a+=x;
 else {
    x=x - a;
  }
}
a+=x;
 parent node is if (a > 0) a+=x;
 else {
  x=x - a;
}
Is in Else Branch: false

x=x - a;
 parent node is if (a > 0) a+=x;
 else {
  x=x - a;
}
Is in Else Branch: true


Part II:
BasicBlock 0 Next is ControlBlock 1
ControlBlock 1 TrueNext is ControlBlock 2
 FalseNext is ControlBlock 4
ControlBlock 2 TrueNext is BasicBlock 3
 FalseNext is ControlBlock 1
ControlBlock 4 TrueNext is ControlBlock 5
FalseNext is NULL
BasicBlock 3 Next is ControlBlock 1
ControlBlock 5 TrueNext is BasicBlock 6
 FalseNext is BasicBlock 7
BasicBlock 6 Next is ControlBlock 4
BasicBlock 7 Next is ControlBlock 4


Now we print each block information
Basic Node No: BasicBlock 0
[x=10;
a=23;
]
Control Node No: ControlBlock 1
x < 1
Control Node No: ControlBlock 2
a > 0
Control Node No: ControlBlock 4
x >= 1
Basic Node No: BasicBlock 3
[a+=x;
]
Control Node No: ControlBlock 5
a > 0
Basic Node No: BasicBlock 6
[a+=x;
]
Basic Node No: BasicBlock 7
[x=x - a;
]
*/