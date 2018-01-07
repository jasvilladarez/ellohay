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

    val artistInviteStream = ArtistInviteStream(listOf(
            ArtistInvite(123,
                    "Test Title",
                    "Artist Grant and Digital Publication",
                    "<p>Test short description</p>",
                    "<p>Test description</p>",
                    Image(
                            ImageVersion("https://assets2.ello.co/uploads/artist_invite/header_image/123/ello-ldpi-c3b8a357.jpg",
                                    ImageVersionMetaData(180, 73, "image/jpeg")),
                            ImageVersion("https://assets2.ello.co/uploads/artist_invite/header_image/123/ello-mdpi-c3b8a357.jpg",
                                    ImageVersionMetaData(375, 153, "image/jpeg")),
                            ImageVersion("https://assets0.ello.co/uploads/artist_invite/header_image/123/ello-hdpi-c3b8a357.jpg",
                                    ImageVersionMetaData(750, 306, "image/jpeg")),
                            ImageVersion("https://assets1.ello.co/uploads/artist_invite/header_image/123/ello-xhdpi-c3b8a357.jpg",
                                    ImageVersionMetaData(1360, 555, "image/jpeg")),
                            ImageVersion("https://assets0.ello.co/uploads/artist_invite/header_image/123/ello-optimized-c3b8a357.jpg",
                                    ImageVersionMetaData(1360, 555, "image/jpeg")),
                            ImageVersion("https://assets2.ello.co/uploads/artist_invite/header_image/123/uploads_2F89d0019e-cd5c-43aa-b38b-316aeca4364e_2FFormat_25202.png",
                                    null)
                    ),
                    Image(
                            null, null, null, null,
                            ImageVersion("https://assets0.ello.co/uploads/artist_invite/logo_image/123/ello-optimized-9da9e868.png",
                                    ImageVersionMetaData(675, 380, "image/png")),
                            ImageVersion("https://assets1.ello.co/uploads/artist_invite/logo_image/123/uploads_2F6ac35e4f-4815-123d4-82d4-c47b9c5202b5_2FFormat.png",
                                    null)
                    ),
                    ArtistInvite.Status.UPCOMING,
                    "2018-01-17T08:00:00.000000Z",
                    "2018-02-13T07:59:00.000000Z")
    ), "2")

    val categories = listOf(Category(
            22, "Shop", "shop",
            Image(
                    null,
                    ImageVersion("https://assets0.ello.co/uploads/category/tile_image/22/ello-small-dcd53e90.png",
                            ImageVersionMetaData(360, 360, "image/png")),
                    ImageVersion("https://assets2.ello.co/uploads/category/tile_image/22/ello-regular-dcd53e90.png",
                            ImageVersionMetaData(800, 800, "image/png")),
                    ImageVersion("https://assets2.ello.co/uploads/category/tile_image/22/ello-large-dcd53e90.png",
                            ImageVersionMetaData(1000, 1000, "image/png")),
                    null,
                    ImageVersion("https://assets2.ello.co/uploads/category/tile_image/22/ello-optimized-e8cd732a.jpg",
                            null)
            ),
            0,
            "Explore a wide collection of items created and sold by individual artists, photographers, independent shops, fashion designers, musicians, developers, writers, graphic designers — Creators. These are unique works made from and for an expressive life.",
            false
    ))

    val postStream = PostStream(Linked(
            listOf(User(123456, "username", "Test Name", Image(
                    null,
                    ImageVersion("https://assets1.ello.co/uploads/user/avatar/123456/ello-small-a4859496.png",
                            ImageVersionMetaData(60, 60, "image/png")),
                    ImageVersion("https://assets0.ello.co/uploads/user/avatar/123456/ello-regular-a4859496.png",
                            ImageVersionMetaData(120, 120, "image/png")),
                    ImageVersion("https://assets2.ello.co/uploads/user/avatar/123456/ello-large-a4859496.png",
                            ImageVersionMetaData(360, 360, "image/png")),
                    null,
                    ImageVersion("https://assets2.ello.co/uploads/user/avatar/123456/ello-37aac31b-d6ba-4703-bcf5-1419b4d901da.jpeg",
                            null)
            ))),
            emptyList(),
            listOf(Category(13, "Makers", "makers", Image(
                    null,
                    ImageVersion("https://assets0.ello.co/uploads/category/tile_image/13/ello-small-228979ab.png",
                            ImageVersionMetaData(360, 360, "image/png")),
                    ImageVersion("https://assets2.ello.co/uploads/category/tile_image/13/ello-regular-228979ab.png",
                            ImageVersionMetaData(800, 800, "image/png")),
                    ImageVersion("https://assets1.ello.co/uploads/category/tile_image/13/ello-large-228979ab.png",
                            ImageVersionMetaData(1000, 1000, "image/png")),
                    null,
                    ImageVersion("https://assets2.ello.co/uploads/category/tile_image/13/ello-optimized-096380b8.jpg",
                            null)), 9,
                    "Explore handmade expressions of creativity from makers of every kind — jewelry, concrete planters, plastic art toys, leather goods — all created with a heightened DIY ethic.",
                    false
            ), Category(
                    22, "Shop", "shop",
                    Image(
                            null,
                            ImageVersion("https://assets0.ello.co/uploads/category/tile_image/22/ello-small-dcd53e90.png",
                                    ImageVersionMetaData(360, 360, "image/png")),
                            ImageVersion("https://assets2.ello.co/uploads/category/tile_image/22/ello-regular-dcd53e90.png",
                                    ImageVersionMetaData(800, 800, "image/png")),
                            ImageVersion("https://assets2.ello.co/uploads/category/tile_image/22/ello-large-dcd53e90.png",
                                    ImageVersionMetaData(1000, 1000, "image/png")),
                            null,
                            ImageVersion("https://assets2.ello.co/uploads/category/tile_image/22/ello-optimized-e8cd732a.jpg",
                                    null)
                    ),
                    0,
                    "Explore a wide collection of items created and sold by individual artists, photographers, independent shops, fashion designers, musicians, developers, writers, graphic designers — Creators. These are unique works made from and for an expressive life.",
                    false
            )),
            listOf(Asset(12345678, Image(
                    ImageVersion("https://assets0.ello.co/uploads/asset/attachment/12345678/ello-ldpi-c5d66bd9.jpg",
                            ImageVersionMetaData(180, 180, "image/jpeg")),
                    ImageVersion("https://assets2.ello.co/uploads/asset/attachment/12345678/ello-mdpi-c5d66bd9.jpg",
                            ImageVersionMetaData(375, 376, "image/jpeg")),
                    ImageVersion("https://assets0.ello.co/uploads/asset/attachment/12345678/ello-hdpi-c5d66bd9.jpg",
                            ImageVersionMetaData(750, 751, "image/jpeg")),
                    ImageVersion("https://assets0.ello.co/uploads/asset/attachment/12345678/ello-xhdpi-c5d66bd9.jpg",
                            ImageVersionMetaData(1080, 1082, "image/jpeg")),
                    ImageVersion("https://assets1.ello.co/uploads/asset/attachment/12345678/ello-optimized-c5d66bd9.jpg",
                            ImageVersionMetaData(1080, 1082, "image/jpeg")),
                    ImageVersion("https://assets0.ello.co/uploads/asset/attachment/12345678/58681263-EAA0-46C6-A97D-7F9FEB12CB11.jpg",
                            null)
            )))
    ), listOf(Post(12345, "dyjcq6gw4dgcdyo2g", "/api/v2/posts/12345", 123456,
            listOf(
                    ImagePostBlock(ImageVersion("https://assets0.ello.co/uploads/asset/attachment/12345678/ello-optimized-c5d66bd9.jpg", null),
                            listOf(AssetLink(12345678))),
                    TextPostBlock("<p>Test Text</p>")
            ),
            listOf(CategoryLink().apply { add(13) },
                    AuthorLink(123456),
                    AssetsListLink().apply { add(12345678) }))
    ), "123")
}