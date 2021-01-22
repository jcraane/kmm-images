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
        Image(uiImage: UIImage(named: Images().IC_ARROW_RIGHT.name, in: Bundle(for: Images.self), compatibleWith: nil)!)
    }
}

struct ContentView_Previews: PreviewProvider {
    static var previews: some View {
        ContentView()
    }
}
