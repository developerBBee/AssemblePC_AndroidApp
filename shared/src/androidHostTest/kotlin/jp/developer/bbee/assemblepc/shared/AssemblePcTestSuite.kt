package jp.developer.bbee.assemblepc.shared

import jp.developer.bbee.assemblepc.shared.domain.model.AssemblyExtTest
import jp.developer.bbee.assemblepc.shared.domain.model.CompositionTest
import jp.developer.bbee.assemblepc.shared.domain.model.DeviceTest
import jp.developer.bbee.assemblepc.shared.domain.model.DeviceTypeTest
import jp.developer.bbee.assemblepc.shared.domain.model.PriceTest
import jp.developer.bbee.assemblepc.shared.domain.use_case.AddAssemblyUseCaseTest
import jp.developer.bbee.assemblepc.shared.domain.use_case.DeleteAssemblyUseCaseTest
import jp.developer.bbee.assemblepc.shared.domain.use_case.DeleteCompositionUseCaseTest
import jp.developer.bbee.assemblepc.shared.domain.use_case.GetCompositionsUseCaseTest
import jp.developer.bbee.assemblepc.shared.domain.use_case.GetDeviceUseCaseTest
import jp.developer.bbee.assemblepc.shared.domain.use_case.RenameAssemblyUseCaseTest
import jp.developer.bbee.assemblepc.shared.domain.use_case.UpdateCurrentCompositionUseCaseTest
import jp.developer.bbee.assemblepc.shared.presentation.DeviceUiStateSuccessTest
import org.junit.runner.RunWith
import org.junit.runners.Suite

/**
 * AssemblePC 共有モジュールのユニットテストスイート。
 *
 * このスイートは commonTest に定義された全テストクラスを集約し、
 * Android JVM ホスト環境でまとめて実行できるようにします。
 *
 * ## 実行方法
 * ```
 * ./gradlew :shared:testDebugUnitTest
 * ```
 *
 * ## テスト構成
 * ### ドメインモデル
 * - [PriceTest]          : 価格モデルの四則演算・比較・フォーマット
 * - [DeviceTypeTest]     : DeviceType 列挙型の変換・バリデーション
 * - [DeviceTest]         : Device → Assembly 変換
 * - [AssemblyExtTest]    : Assembly リストの CompositionItem への変換
 * - [CompositionTest]    : 構成モデルの取得・レビュー・期限判定
 *
 * ### ユースケース
 * - [AddAssemblyUseCaseTest]              : アセンブリ追加
 * - [DeleteAssemblyUseCaseTest]           : アセンブリ削除
 * - [GetDeviceUseCaseTest]                : デバイス一覧取得（Loading/Success/Failure）
 * - [GetCompositionsUseCaseTest]          : 構成一覧取得
 * - [RenameAssemblyUseCaseTest]           : アセンブリ名変更
 * - [DeleteCompositionUseCaseTest]        : 構成削除
 * - [UpdateCurrentCompositionUseCaseTest] : 現在の構成更新
 *
 * ### プレゼンテーション
 * - [DeviceUiStateSuccessTest] : デバイス一覧 UI 状態（ソート・検索・選択ロジック）
 */
@RunWith(Suite::class)
@Suite.SuiteClasses(
    // ドメインモデル
    PriceTest::class,
    DeviceTypeTest::class,
    DeviceTest::class,
    AssemblyExtTest::class,
    CompositionTest::class,
    // ユースケース
    AddAssemblyUseCaseTest::class,
    DeleteAssemblyUseCaseTest::class,
    GetDeviceUseCaseTest::class,
    GetCompositionsUseCaseTest::class,
    RenameAssemblyUseCaseTest::class,
    DeleteCompositionUseCaseTest::class,
    UpdateCurrentCompositionUseCaseTest::class,
    // プレゼンテーション
    DeviceUiStateSuccessTest::class,
)
class AssemblePcTestSuite
