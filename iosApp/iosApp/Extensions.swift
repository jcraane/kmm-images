//
//  Extensions.swift
//  iosApp
//
//  Created by Jamie Craane on 21/07/2021.
//  Copyright © 2021 orgName. All rights reserved.
//

import Foundation
import SwiftUI
import shared

/**
 * Creates a SwiftUI Image from an image in Images. Use like this: Images().IC_FLAG_NL.swiftUIImage
 */
public extension shared.Image {

    private static var sharedBundle = Bundle(for: Images.self)

    var uiImage: UIImage? {
        let sharedImage = UIImage(named: name, in: shared.Image.sharedBundle, compatibleWith: nil)
        return sharedImage
    }

    var swiftUIImage: SwiftUI.Image {
        return SwiftUI.Image(name, bundle: shared.Image.sharedBundle)
    }
}
