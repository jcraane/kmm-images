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
             Image(uiImage: UIImage(named: Images().KEYBOARD.name)!)
        }
}

struct ContentView_Previews: PreviewProvider {
    static var previews: some View {
        ContentView()
    }
}
