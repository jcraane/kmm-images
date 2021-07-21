import SwiftUI
import shared

func greet() -> String {
    return "Images"
}

struct ContentView: View {
//   var body: some View {
//        Text(greet())
//    }
    var body: some View {
        Images().IC_FLAG_NL.swiftUIImage        
    }
}

struct ContentView_Previews: PreviewProvider {
    static var previews: some View {
        ContentView()
    }
}


