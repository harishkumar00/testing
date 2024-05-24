import * as React from 'react';

import { StyleSheet, View, Text, TouchableOpacity } from 'react-native';
import { startActivity } from 'react-native-rently-meari';

export default function App() {

  return (
    <View style={styles.container}>
      <TouchableOpacity style={{
        height: 40,
        width: 60,
        backgroundColor: "blue",
        borderRadius: 10,
        borderWidth: 1,
      }}
        onPress={() => {
          startActivity({ deviceId: "14439729" })
        }}>
        <Text>Start Meari Activity</Text>
      </TouchableOpacity>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    alignItems: 'center',
    justifyContent: 'center',
  }
});
