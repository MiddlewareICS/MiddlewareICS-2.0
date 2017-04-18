//var swingNames = JavaImporter();   
//swingNames.importPackage(Packages.java.lang);   
//  
//obj = {a:1, b:['x','y']}   
//next = isPrime   
//flag = isPrime(5)  
//
//with (swingNames) {   
 System.out.println("\n###   Here is custom1.js   ###\n");  
// System.out.println("\n###   The result of cunstom javascript function, isPrime(5), is: "+ flag +"   ###\n"); 
// var i=0;
// while(i<10){
//	 System.out.println("\n###   The Temperature of my room is 25 'C   ###\n");
//	 sleep(1000);
//	 i++;
// }
//}    


function isPrime (num)   
{   
 java.lang.System.out.println("in isPrime(num)");   
    if (num <= 1) {   
        java.lang.System.out.println("Please enter a positive integer >= 2.")   
        return false  
    }          
    var prime = true  
    var sqrRoot = Math.round(Math.sqrt(num))   
       
    for (var n = 2; prime & n <= sqrRoot; ++n) {   
        prime = (num % n != 0)   
    }        
    return prime   
}   

function sleep(numberMillis) {  
    var now = new Date();  
    var exitTime = now.getTime() + numberMillis;  
    while (true) {  
        now = new Date();  
        if (now.getTime() > exitTime)  
            return;  
    }  
}  

