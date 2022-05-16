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
        VStack {
            Images().HELLO.swiftUIImage
            Images().PNG_ICON.swiftUIImage
        }
    }
}

struct ContentView_Previews: PreviewProvider {
    static var previews: some View {
        Group {
            ContentView()
        }
    }
}


