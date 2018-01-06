/*
 * MIT License
 *
 * Copyright (c) 2018 Jasmine Villadarez
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package io.github.jasvilladarez.domain.repository.browse

import io.github.jasvilladarez.domain.entity.*

internal object BrowseRepositoryTestObject {

    val editorialStream = EditorialStream(listOf(
            Editorial(123,
                    Editorial.EditorialType.POST,
                    "Test Title",
                    "\"Test Subtitle\"",
                    "<p>\"Test rendered subtitle\"</p>",
                    Image(
                            ImageVersion("https://assets0.ello.co/uploads/editorial/one_by_one_image/123/ello-ldpi-e910e8f4.jpg",
                                    ImageVersionMetaData(180, 180, "image/jpeg")),
                            ImageVersion("https://assets2.ello.co/uploads/editorial/one_by_one_image/123/ello-mdpi-e910e8f4.jpg",
                                    ImageVersionMetaData(375, 375, "image/jpeg")),
                            ImageVersion("https://assets1.ello.co/uploads/editorial/one_by_one_image/123/ello-hdpi-e910e8f4.jpg",
                                    ImageVersionMetaData(750, 750, "image/jpeg")),
                            ImageVersion("https://assets0.ello.co/uploads/editorial/one_by_one_image/123/ello-xhdpi-e910e8f4.jpg",
                                    ImageVersionMetaData(1500, 1500, "image/jpeg")),
                            ImageVersion("https://assets0.ello.co/uploads/editorial/one_by_one_image/123/ello-optimized-e910e8f4.jpg",
                                    ImageVersionMetaData(1920, 1920, "image/jpeg")),
                            ImageVersion("https://assets1.ello.co/uploads/editorial/one_by_one_image/123/uploads_2F8bbf59ec-9cd6-4f4e-aeb2-e536459787e8_2Fello-optimized-46a4a289.jpg", null)
                    ),
                    listOf(PostLink("123467", "/api/v2/posts/14503035")))
    ), "101981")
}