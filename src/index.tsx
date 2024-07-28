import { NativeModules, Platform } from 'react-native';

const LINKING_ERROR =
  `The package 'react-native-rently-meari' doesn't seem to be linked. Make sure: \n\n` +
  Platform.select({ ios: "- You have run 'pod install'\n", default: '' }) +
  '- You rebuilt the app after installing the package\n' +
  '- You are not using Expo Go\n';

const RentlyMeari = NativeModules.RentlyMeari
  ? NativeModules.RentlyMeari
  : new Proxy(
      {},
      {
        get() {
          throw new Error(LINKING_ERROR);
        },
      }
    );

export type StartPreviewType = {
  deviceId: string;
};

export function startActivity(params: StartPreviewType): Promise<string> {
  return RentlyMeari.openLivePreview(params);
}

export type loginType = {
  account: string;
  password: string;
  countryCode: string;
  phoneCode: string;
};

export function login(params: loginType): Promise<string> {
  return RentlyMeari.login(params);
}

export const getTokenForQRCode = async (): Promise<any> => {
  const data = await RentlyMeari.getTokenForQRCode();

  return data;
};
