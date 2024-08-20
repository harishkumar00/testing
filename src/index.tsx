import { AppRegistry, NativeModules, Platform } from 'react-native';
import Main from '../react-native/Main';

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

export type loginType = {
  account: string;
  password: string;
  countryCode: string;
  phoneCode: string;
};

export const login = async (params: loginType): Promise<any> => {
  const data = await RentlyMeari.login(params);

  return data;
};

export type StartPreviewType = {
  deviceId: string;
};

export const startActivity = (params: StartPreviewType): Promise<any> => {
  return RentlyMeari.openLivePreview(params);
};

export type OpenJSScreenType = {
  name: string;
};

export const openJSScreen = (params: OpenJSScreenType): Promise<any> => {
  return RentlyMeari.openJSScreen(params);
};

export const getTokenForQRCode = async (): Promise<any> => {
  const data = await RentlyMeari.getTokenForQRCode();

  return data;
};

export type setupPushNotificationType = {
  token: string;
};

export const setupPushNotification = async (
  params: setupPushNotificationType
): Promise<any> => {
  const data = await RentlyMeari.setupPushNotification(params);

  return data;
};

AppRegistry.registerComponent('Home', () => Main);
